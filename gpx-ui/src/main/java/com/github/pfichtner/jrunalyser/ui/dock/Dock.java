package com.github.pfichtner.jrunalyser.ui.dock;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.eknet.swing.task.Mode.BLOCKING;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.eknet.swing.task.AbstractTask;
import org.eknet.swing.task.TaskManager;
import org.eknet.swing.task.Tracker;
import org.eknet.swing.task.impl.TaskManagerImpl;
import org.eknet.swing.task.ui.TaskGlassPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;

import com.ezware.dialog.task.TaskDialogs;
import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.Distances;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.Durations;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxMarshaller;
import com.github.pfichtner.jrunalyser.base.data.jaxb.GpxUnmarshaller;
import com.github.pfichtner.jrunalyser.base.data.stat.Functions.StatisticsProviders;
import com.github.pfichtner.jrunalyser.base.data.stat.Functions.Statisticss;
import com.github.pfichtner.jrunalyser.base.data.stat.Statistics;
import com.github.pfichtner.jrunalyser.base.data.track.DefaultTrack;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.StatisticsProvider;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.github.pfichtner.jrunalyser.base.datasource.CachingDatasourceFascadeProxy;
import com.github.pfichtner.jrunalyser.base.datasource.DataSourceDatasourceFascadeAdapter;
import com.github.pfichtner.jrunalyser.base.datasource.Datasource;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascadeEvent;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascadeEvent.Type;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascadeListener;
import com.github.pfichtner.jrunalyser.base.datasource.InMemoryDataSource;
import com.github.pfichtner.jrunalyser.base.datasource.SerializatingDatasourceFascade;
import com.github.pfichtner.jrunalyser.base.datasource.StatCalculatorDatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Injector;
import com.github.pfichtner.jrunalyser.ui.base.DefaultGridData;
import com.github.pfichtner.jrunalyser.ui.base.DockPlugin;
import com.github.pfichtner.jrunalyser.ui.base.GridData;
import com.github.pfichtner.jrunalyser.ui.base.GridDataProvider;
import com.github.pfichtner.jrunalyser.ui.base.UiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.EventBusMessage;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackAdded;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackRemoved;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class Dock {

	private static final I18N i18n = I18N
			.builder(Dock.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	public static I18N getI18n() {
		return i18n;
	}

	/**
	 * Listener that checks that all message sent over the event bus are
	 * annotated using @EventBusMessage.
	 * 
	 * @author Peter Fichtner
	 */
	private static class CheckAnnotationListener {

		private final Class<? extends Annotation> expected;

		public CheckAnnotationListener(Class<? extends Annotation> expected) {
			this.expected = expected;
		}

		@Subscribe
		public void anyMessage(Object message) {
			log.debug("EventBus message: {}", message); //$NON-NLS-1$
			Class<? extends Object> msgType = message.getClass();
			try {
				checkState(msgType.isAnnotationPresent(this.expected),
						"%s must be annotated using %s", msgType.getName(), //$NON-NLS-1$
						this.expected.getName());
			} catch (Exception e) {
				showError(e);
			}

		}
	}

	private static final Logger log = LoggerFactory.getLogger(Dock.class);

	private static class MyDataSource extends InMemoryDataSource {

		private final File base;

		public MyDataSource(File base) {
			this.base = checkNotNull(base, "Directory must not be null"); //$NON-NLS-1$
		}

		@Override
		public Track addTrack(Track track) {
			File file = new File(this.base, createFileName(track) + ".gpx"); //$NON-NLS-1$
			checkState(!file.exists(), "File %s already exists!", //$NON-NLS-1$
					file.getName());
			try {
				GpxMarshaller.writeTrack(file, track);
				return addTrack(file);
			} catch (IOException e) {
				throw Throwables.propagate(e);
			}
		}

		public Track addTrack(File file) throws IOException {
			Track track = GpxUnmarshaller.loadTrack(file);
			// recalc key
			Track result = new DefaultTrack(new FileId(file),
					track.getMetadata(), track.getWaypoints(),
					track.getSegments(), track.getStatistics());
			super.addTrack(result);
			return result;
		}

		private String createFileName(Track track) {
			checkState(
					!checkNotNull(checkNotNull(track, "Track must not be null") //$NON-NLS-1$
							.getTrackpoints(), "Waypoints must not be null").isEmpty(), //$NON-NLS-1$
					"Waypoints must not be empty"); //$NON-NLS-1$
			return formatGoogleStyle(track.getTrackpoints().get(0).getTime()
					.longValue());
		}

		private String formatGoogleStyle(long value) {
			return new SimpleDateFormat("dd_MM_yyyy HH_mm").format(new Date( //$NON-NLS-1$
					value));
		}
	}

	public static class FileId implements Id {

		private final File file;

		public FileId(File file) {
			this.file = file;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.file == null) ? 0 : this.file.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FileId other = (FileId) obj;
			if (this.file == null) {
				if (other.file != null)
					return false;
			} else if (!this.file.equals(other.file))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "FileId [file=" + this.file + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	private static final String TITLE = i18n
			.getText("com.github.pfichtner.jrunalyser.ui.dock.Dock.title"); //$NON-NLS-1$
	private static final String COMMON_WPTS = "common.wpts"; //$NON-NLS-1$

	public static void main(String[] args) throws IOException,
			InterruptedException, InvocationTargetException {
		setupExceptionHandler();
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				init();
			}
		});

	}

	private static void init() {
		final EventBus eventBus = new EventBus();
		eventBus.register(new CheckAnnotationListener(EventBusMessage.class));

		final DatasourceFascade dsf = createDatasourceFascade();
		initInBackground(dsf);
		dsf.addListener(new DatasourceFascadeListener() {
			@Override
			public void contentChanged(DatasourceFascadeEvent ev) {
				Type type = ev.getType();
				switch (type) {
				case ADDED:
					try {
						// load it via dsf to ensure it has stats
						eventBus.post(new TrackAdded(dsf.loadTrack(ev
								.getTrack().getId())));
					} catch (IOException e) {
						throw Throwables.propagate(e);
					}
					break;
				case REMOVED:
					eventBus.post(new TrackRemoved(ev.getTrack()));
					break;
				// TODO We should fire TrackLoaded on MODIFIED
				default:
					break;
				}
			}
		});

		JFrame frame = new JFrame(TITLE);
		final CControl control = new CControl(frame);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				saveState(control);
				((JFrame) e.getSource())
						.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});

		control.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
		frame.add(control.getContentArea());

		GridData defaultGridData = new DefaultGridData(1, 3, 3, 1);
		CGrid grid = new CGrid(control);
		Iterable<UiPlugin> plugins = loadPlugins();
		for (UiPlugin plugin : plugins) {
			log.info("Adding plugin type {}: {}", plugin.getClass().getName(), //$NON-NLS-1$
					plugin);
			register(plugin, eventBus, dsf, frame);
			if (plugin instanceof DockPlugin) {
				DockPlugin dockPlugin = (DockPlugin) plugin;
				GridData gd = dockPlugin instanceof GridDataProvider ? ((GridDataProvider) dockPlugin)
						.getGridData() : defaultGridData;
				grid.add(gd.getGridX(), gd.getGridY(), gd.getGridWidth(),
						gd.getGridHeight(), createDockable(dockPlugin));
			}
		}
		control.getContentArea().deploy(grid);

		RootMenuPiece menuBuilder = new RootMenuPiece(
				i18n.getText("com.github.pfichtner.jrunalyser.ui.dock.Dock.mWindows.title"), //$NON-NLS-1$
				false);
		menuBuilder.add(new SingleCDockableListMenuPiece(control));
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu(dsf, frame, eventBus));
		menuBar.add(menuBuilder.getMenu());
		MenuHack h = new MenuHack(eventBus);
		menuBar.add(h.getSegmentMenu());
		menuBar.add(h.getHighlightMenu());
		frame.setJMenuBar(menuBar);

		loadState(control);

		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		frame.setBounds(20, 20, screenSize.width - 80, screenSize.height - 60);

		// center
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		TaskManager tm = new TaskManagerImpl();
		frame.setGlassPane(new TaskGlassPane(tm));
	}

	private static void initInBackground(final DatasourceFascade dsf) {
		new Thread() {
			{
				start();
			}

			@Override
			public void run() {
				try {
					Set<Id> trackIds = dsf.getTrackIds();
					if (!trackIds.isEmpty()) {
						{
							Distance highest = greatestDistance(dsf, trackIds);
							for (Distance distance : Distances
									.distanceIterator(highest)) {
								log.info("Precalculating {}", distance); //$NON-NLS-1$
								dsf.listTracks(distance);
							}
						}
						{
							Duration highest = greatestDuration(dsf, trackIds);
							for (Duration duration : Durations
									.durationIterator(highest)) {
								log.info("Precalculating {}", duration); //$NON-NLS-1$
								dsf.listTracks(duration);
							}

						}
					}
				} catch (IOException e) {
					throw Throwables.propagate(e);
				}
			}

			private Distance greatestDistance(final DatasourceFascade dsf,
					Set<Id> trackIds) throws IOException {
				return getMax(getStats(dsf, trackIds).transform(
						Statisticss.distance));
			}

			private Duration greatestDuration(final DatasourceFascade dsf,
					Set<Id> trackIds) throws IOException {
				return getMax(getStats(dsf, trackIds).transform(
						Statisticss.duration));
			}

			private <T extends Comparable<T>> T getMax(
					FluentIterable<T> iterable) {
				return Ordering.natural().max(iterable);
			}

			private FluentIterable<Statistics> getStats(
					final DatasourceFascade dsf, Set<Id> trackIds)
					throws IOException {
				return FluentIterable.from(trackIds).transform(loadTrack(dsf))
						.transform(StatisticsProviders.statistics);
			}

			private Function<Id, StatisticsProvider> loadTrack(
					final DatasourceFascade dsf) {
				return new Function<Id, StatisticsProvider>() {
					@Override
					public StatisticsProvider apply(Id id) {
						try {
							return dsf.loadTrack(id);
						} catch (IOException e) {
							throw Throwables.propagate(e);
						}
					}
				};
			}

		};
	}

	private static DatasourceFascade createDatasourceFascade() {
		final File baseDir = new File(System.getProperty("user.home"), "gpx"); //$NON-NLS-1$ //$NON-NLS-2$
		checkState(baseDir.exists() || baseDir.mkdirs(),
				"Cannot create directory %s", baseDir); //$NON-NLS-1$

		Datasource datasource = Suppliers.background(
				new Supplier<Datasource>() {
					@Override
					public Datasource get() {
						try {
							return createDatasource(baseDir);
						} catch (IOException e) {
							throw Throwables.propagate(e);
						}
					}

				}, Datasource.class);
		DataSourceDatasourceFascadeAdapter dsf0 = new DataSourceDatasourceFascadeAdapter(
				datasource);
		SerializatingDatasourceFascade dsf2 = new SerializatingDatasourceFascade(
				baseDir, dsf0);
		CachingDatasourceFascadeProxy dsf3 = new CachingDatasourceFascadeProxy(
				dsf2);
		StatCalculatorDatasourceFascade dsf4 = new StatCalculatorDatasourceFascade(
				dsf3);
		CachingDatasourceFascadeProxy dsf5 = new CachingDatasourceFascadeProxy(
				dsf4);
		return dsf5;
	}

	private static Datasource createDatasource(final File baseDir)
			throws IOException {
		MyDataSource dataSource = new MyDataSource(baseDir);

		File cwps = new File(baseDir, COMMON_WPTS);
		if (cwps.canRead()) {
			for (WayPoint wayPoint : GpxUnmarshaller.loadTrack(cwps)
					.getWaypoints()) {
				dataSource.addCommonWaypoint(wayPoint);
			}
		}

		try {
			ExecutorService es = Executors.newFixedThreadPool(Runtime
					.getRuntime().availableProcessors());
			es.invokeAll(createCallables(dataSource, getFiles(dataSource)));
			es.shutdown();
		} catch (InterruptedException e) {
			throw Throwables.propagate(e);
		}
		return dataSource;
	}

	private static File[] getFiles(MyDataSource dataSource) {
		return dataSource.base.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile() && !file.getName().equals(COMMON_WPTS);
			}
		});
	}

	private static Collection<Callable<Track>> createCallables(
			final MyDataSource dataSource, File[] listFiles) {
		List<Callable<Track>> result = Lists
				.newArrayListWithExpectedSize(listFiles.length);
		for (final File file : listFiles) {
			result.add(new Callable<Track>() {
				@Override
				public Track call() throws IOException {
					log.debug("Loading {}", file); //$NON-NLS-1$
					Track addedTrack;
					addedTrack = dataSource.addTrack(file);
					log.info("{} loaded", file); //$NON-NLS-1$
					return addedTrack;
				}
			});

		}
		return result;
	}

	private static Iterable<UiPlugin> loadPlugins() {
		return Lists
				.newArrayList(ServiceLoader.load(UiPlugin.class).iterator());
	}

	private static boolean loadState(CControl control) {
		File layoutFile = getLayoutFile();
		if (!layoutFile.exists()) {
			return false;
		}
		try {
			control.readXML(layoutFile);
			return true;
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private static void saveState(CControl control) {
		try {
			control.writeXML(getLayoutFile());
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	private static File getLayoutFile() {
		return new File(getSettingBaseDir(), "main-layout.xml"); //$NON-NLS-1$
	}

	private static File getSettingBaseDir() {
		File baseDir = new File(System.getProperty("user.home"), "." //$NON-NLS-1$ //$NON-NLS-2$
				+ TITLE.toLowerCase());
		checkState(baseDir.exists() || baseDir.mkdirs(),
				"Cannot create directory %s", baseDir); //$NON-NLS-1$
		return baseDir;
	}

	private static void setupExceptionHandler() {
		// TODO http://www.javaspecialists.eu/archive/Issue196.html
		final UncaughtExceptionHandler exceptionHandler = new UncaughtExceptionHandler() {
			public void uncaughtException(final Thread thread, final Throwable t) {
				showError(t);
			}

		};
		Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
		System.setProperty(
				"sun.awt.exception.handler", exceptionHandler.getClass().getName()); //$NON-NLS-1$
	}

	private static void showError(final Throwable t) {
		try {
			TaskDialogs.showException(t);
		} catch (final Throwable t2) {
			/*
			 * don't let the Throwable get thrown out, will cause infinite
			 * looping!
			 */
			t2.printStackTrace();
		}
	}

	private static JMenu createFileMenu(
			final DatasourceFascade datasourceFascade, final JFrame parent,
			final EventBus eventBus) {
		JMenu jMenu = new JMenu(TITLE);
		JMenuItem menuItem = new JMenuItem(
				i18n.getText("com.github.pfichtner.jrunalyser.ui.dock.Dock.miAddGpx.title")); //$NON-NLS-1$

		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
					File[] selectedFiles = chooser.getSelectedFiles();

					Component glassPane = parent.getGlassPane();
					if (glassPane instanceof TaskGlassPane) {
						TaskGlassPane taskGlassPane = (TaskGlassPane) glassPane;
						TaskManager tm = taskGlassPane.getTaskManager();

						int cpus = Runtime.getRuntime().availableProcessors();
						List<List<File>> partitions = Lists.partition(
								Arrays.asList(selectedFiles),
								(selectedFiles.length + cpus - 1) / cpus);
						for (final List<File> files : partitions) {
							tm.create(
									new AbstractTask<Void, Void>(
											i18n.getText("com.github.pfichtner.jrunalyser.ui.dock.Dock.importDialog.title"), BLOCKING) { //$NON-NLS-1$
										@Override
										public Void execute(
												Tracker<Void> tracker) {
											int i = 0;
											for (File file : files) {
												tracker.setProgress(0,
														files.size(), i++);
												tracker.setPhase(i18n
														.getText(
																"com.github.pfichtner.jrunalyser.ui.dock.Dock.importDialog.format", file)); //$NON-NLS-1$
												try {
													datasourceFascade
															.addTrack(GpxUnmarshaller
																	.loadTrack(file));
												} catch (IOException e) {
													throw Throwables
															.propagate(e);
												}
											}
											return null;

										}
									}).execute();
						}

					} else {
						for (File selectedFile : selectedFiles) {
							try {
								datasourceFascade.addTrack(GpxUnmarshaller
										.loadTrack(selectedFile));
							} catch (IOException e) {
								throw Throwables.propagate(e);
							}
						}
					}

				}
			}
		});
		jMenu.add(menuItem);
		return jMenu;
	}

	private static DefaultSingleCDockable createDockable(final DockPlugin plugin) {
		DefaultSingleCDockable dockable = new DefaultSingleCDockable(
				plugin.getId(), plugin.getTitle(), plugin.getPanel());
		dockable.setCloseable(true);
		return dockable;
	}

	private static <T> T register(final T plugin, EventBus eventBus,
			DatasourceFascade dsf, Component parent) {
		eventBus.register(plugin);
		Injector.inject(plugin, EventBus.class, eventBus);
		Injector.inject(plugin, DatasourceFascade.class, dsf);
		Injector.inject(plugin, Component.class, parent);
		return plugin;
	}

}
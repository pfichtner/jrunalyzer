package com.github.pfichtner.jrunalyser.ui.tracklist;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pfichtner.jrunalyser.base.data.SegmentationUnit;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.datasource.DatasourceFascade;
import com.github.pfichtner.jrunalyser.di.Inject;
import com.github.pfichtner.jrunalyser.ui.base.AbstractUiPlugin;
import com.github.pfichtner.jrunalyser.ui.base.i18n.I18N;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.AdditionalTracks;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackAdded;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackLoaded;
import com.github.pfichtner.jrunalyser.ui.dock.ebus.TrackRemoved;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class TrackListPlugin extends AbstractUiPlugin {

	private static final Logger log = LoggerFactory
			.getLogger(TrackListPlugin.class);

	private static final I18N i18n = I18N
			.builder(TrackListPlugin.class)
			.withParent(
					com.github.pfichtner.jrunalyser.ui.base.UiPlugins.getI18n())
			.build();

	public static I18N getI18n() {
		return i18n;
	}

	private final JPanel panel = new JPanel(new BorderLayout());
	private final TrackTable table;

	private DatasourceFascade dsf;

	public TrackListPlugin() {
		this.table = new TrackTable(new TrackTableModel());
		// sort by startdate
		this.table.getRowSorter()
				.toggleSortOrder(TrackTableModel.COL_STARTDATE);
		this.panel.add(new JScrollPane(this.table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}

	@Override
	public String getTitle() {
		return getI18n()
				.getText(
						"com.github.pfichtner.jrunalyser.ui.tracklist.TrackListPlugin.title"); //$NON-NLS-1$
	}

	@Override
	public JPanel getPanel() {
		return this.panel;
	}

	@Inject
	public void setEventBus(EventBus eventBus) {
		this.table.addEventBusPoster(eventBus);
	}

	@Inject
	public void setDatasourceFascade(final DatasourceFascade dsf)
			throws IOException, InterruptedException, ExecutionException {
		this.dsf = dsf;

		this.table.setEnabled(false);
		final Iterable<SegmentationUnit> segmentationUnits = getSegmentationUnitsFromHeader();

		new SwingWorker<Void, TrackRow>() {
			@Override
			protected Void doInBackground() throws Exception {
				final Collection<Callable<TrackRow>> callables = createCallables(
						dsf.getTrackIds(), segmentationUnits);
				log.debug(
						"Created {} callables", Integer.valueOf(callables.size())); //$NON-NLS-1$
				ExecutorCompletionService<TrackRow> scattered = scatter(callables);
				log.debug("Scattered", Integer.valueOf(callables.size())); //$NON-NLS-1$
				gather(scattered, callables.size());
				log.debug("Gathered", Integer.valueOf(callables.size())); //$NON-NLS-1$
				return null;
			}

			private void gather(ExecutorCompletionService<TrackRow> ecs,
					int size) {
				for (int i = 0; i < size; i++) {
					try {
						publish(ecs.take().get());
					} catch (final InterruptedException e) {
						Thread.currentThread().interrupt();
					} catch (final ExecutionException e) {
						throw Throwables.propagate(e);
					}
				}
			}

			private List<Callable<TrackRow>> createCallables(Set<Id> ids,
					final Iterable<SegmentationUnit> segmentationUnits) {
				List<Callable<TrackRow>> callables = Lists
						.newArrayListWithCapacity(ids.size());
				for (final Id id : ids) {
					callables.add(new Callable<TrackRow>() {
						@Override
						public TrackRow call() throws Exception {
							TrackRow trackRow = new TrackRow(dsf, id);
							for (SegmentationUnit segmentationUnit : segmentationUnits) {
								trackRow.getBestSegment(segmentationUnit);
							}
							return trackRow;
						}

					});
				}
				return callables;
			}

			private ExecutorCompletionService<TrackRow> scatter(
					final Collection<Callable<TrackRow>> callables) {
				final ExecutorService es = Executors.newFixedThreadPool(Runtime
						.getRuntime().availableProcessors());
				final ExecutorCompletionService<TrackRow> ecs = new ExecutorCompletionService<TrackRow>(
						es);
				for (final Callable<TrackRow> callable : callables) {
					ecs.submit(callable);
				}
				es.shutdown();
				return ecs;
			}

			protected void process(List<TrackRow> trackRows) {
				TrackListPlugin.this.table.getModel().addRows(trackRows);
			};

			@Override
			protected void done() {
				// call get to make sure any exceptions thrown during
				// doInBackground() are thrown again
				try {
					get();
				} catch (final InterruptedException e) {
					throw Throwables.propagate(e);
				} catch (final ExecutionException e) {
					throw Throwables.propagate(e);
				} finally {
					TrackListPlugin.this.table.setEnabled(true);
				}
			}

		}.execute();
	}

	private Iterable<SegmentationUnit> getSegmentationUnitsFromHeader() {
		List<Object> headerValues = Lists.newArrayList();
		for (int i = 0; i < this.table.getColumnModel().getColumnCount(); i++) {
			headerValues.add(this.table.getModel().getHeaderValue(i));
		}
		return Iterables.filter(headerValues, SegmentationUnit.class);
	}

	protected DatasourceFascade getDatasourceFascade() {
		return this.dsf;
	}

	@Subscribe
	public void addTrack(TrackAdded message) {
		try {
			TrackListPlugin.this.table.getModel().addRow(
					new TrackRow(this.dsf, message.getTrack().getId()));
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Subscribe
	public void removeTrack(TrackRemoved message) {
		TrackListPlugin.this.table.getModel().removeRow(
				message.getTrack().getId());
	}

	@Subscribe
	public void setTrack(TrackLoaded message) throws IOException {
		this.table.setTrack(message.getTrack());
	}

	@Subscribe
	public void setAdditionalTracks(AdditionalTracks message)
			throws IOException {
		this.table.setAdditionalTracks(message.getTracks());
	}

}

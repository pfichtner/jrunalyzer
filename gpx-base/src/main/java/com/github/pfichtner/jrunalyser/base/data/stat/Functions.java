package com.github.pfichtner.jrunalyser.base.data.stat;

import static com.google.common.base.Functions.compose;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.pfichtner.jrunalyser.base.data.Distance;
import com.github.pfichtner.jrunalyser.base.data.DistanceUnit;
import com.github.pfichtner.jrunalyser.base.data.Duration;
import com.github.pfichtner.jrunalyser.base.data.Gradient;
import com.github.pfichtner.jrunalyser.base.data.Link;
import com.github.pfichtner.jrunalyser.base.data.LinkedTrackPoint;
import com.github.pfichtner.jrunalyser.base.data.Speed;
import com.github.pfichtner.jrunalyser.base.data.WayPoint;
import com.github.pfichtner.jrunalyser.base.data.track.Id;
import com.github.pfichtner.jrunalyser.base.data.track.Metadata;
import com.github.pfichtner.jrunalyser.base.data.track.StatisticsProvider;
import com.github.pfichtner.jrunalyser.base.data.track.Track;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public final class Functions {

	public static class Tracks {

		public static final Function<Track, Id> id = new Function<Track, Id>() {
			public Id apply(Track track) {
				return track.getId();
			}
		};

		public static final Function<Track, List<? extends LinkedTrackPoint>> trackpoints = new Function<Track, List<? extends LinkedTrackPoint>>() {
			public List<? extends LinkedTrackPoint> apply(Track track) {
				return track.getTrackpoints();
			}
		};

		public static final Function<Track, Metadata> metadata = new Function<Track, Metadata>() {
			public Metadata apply(Track track) {
				return track.getMetadata();
			}
		};

	}

	public static class Metadatas {

		public static final Function<Metadata, String> name = new Function<Metadata, String>() {
			public String apply(Metadata metadata) {
				return metadata.getName();
			}
		};
		public static final Function<Metadata, String> description = new Function<Metadata, String>() {
			public String apply(Metadata metadata) {
				return metadata.getDescription();
			}
		};

		public static final Function<Metadata, Double> maxLatitude = new Function<Metadata, Double>() {
			public Double apply(Metadata metadata) {
				return Double.valueOf(metadata.getMaxLatitude());
			}
		};

		public static final Function<Metadata, Double> minLatitude = new Function<Metadata, Double>() {
			public Double apply(Metadata metadata) {
				return Double.valueOf(metadata.getMinLatitude());
			}
		};

		public static final Function<Metadata, Double> maxLongitude = new Function<Metadata, Double>() {
			public Double apply(Metadata metadata) {
				return Double.valueOf(metadata.getMaxLongitude());
			}
		};

		public static final Function<Metadata, Double> minLongitude = new Function<Metadata, Double>() {
			public Double apply(Metadata metadata) {
				return Double.valueOf(metadata.getMinLongitude());
			}
		};
	}

	public static class StatisticsProviders {

		public static final Function<StatisticsProvider, Statistics> statistics = new Function<StatisticsProvider, Statistics>() {
			@Override
			public Statistics apply(StatisticsProvider provider) {
				return provider == null ? null : provider.getStatistics();
			}
		};

	}

	public static class Statisticss {

		public static final Function<Statistics, Distance> distance = new Function<Statistics, Distance>() {
			@Override
			public Distance apply(Statistics statistics) {
				return statistics.getDistance();
			}
		};

		public static final Function<Statistics, Duration> duration = new Function<Statistics, Duration>() {
			@Override
			public Duration apply(Statistics statistics) {
				return statistics.getDuration();
			}
		};

		public static final Function<Statistics, LinkedTrackPoint> maxSpeed = new Function<Statistics, LinkedTrackPoint>() {
			@Override
			public LinkedTrackPoint apply(Statistics statistics) {
				return statistics.getMaxSpeed();
			}
		};

		public static final Function<Statistics, WayPoint> maxEle = new Function<Statistics, WayPoint>() {
			@Override
			public WayPoint apply(Statistics statistics) {
				return statistics.getMaxElevation();
			}
		};

		public static final Function<Statistics, WayPoint> minEle = new Function<Statistics, WayPoint>() {
			@Override
			public WayPoint apply(Statistics statistics) {
				return statistics.getMinElevation();
			}
		};

		public static final Function<Statistics, Integer> eleDiff = new Function<Statistics, Integer>() {
			@Override
			public Integer apply(Statistics statistics) {
				return Integer.valueOf(statistics.getMaxElevation()
						.getElevation().intValue()
						- statistics.getMinElevation().getElevation()
								.intValue());
			}
		};

		public static final Function<Statistics, Speed> avgSpeed = new Function<Statistics, Speed>() {
			@Override
			public Speed apply(Statistics statistics) {
				return statistics.getAvgSpeed();
			}
		};

	}

	public static class WayPoints {

		private static final Function<WayPoint, Integer> elevation = new Function<WayPoint, Integer>() {
			public Integer apply(WayPoint wayPoint) {
				return wayPoint.getElevation();
			}
		};
		private static final Function<WayPoint, Double> latitude = new Function<WayPoint, Double>() {
			public Double apply(WayPoint wayPoint) {
				return Double.valueOf(wayPoint.getLatitude());
			}
		};
		private static final Function<WayPoint, Double> longitude = new Function<WayPoint, Double>() {
			public Double apply(WayPoint wayPoint) {
				return Double.valueOf(wayPoint.getLongitude());
			}
		};
		private static final Function<WayPoint, Long> time = new Function<WayPoint, Long>() {
			public Long apply(WayPoint wayPoint) {
				return wayPoint.getTime();
			}
		};

		private WayPoints() {
			super();
		}

		public static Function<WayPoint, Integer> elevation() {
			return elevation;
		}

		public static Function<WayPoint, Double> latitude() {
			return latitude;
		}

		public static Function<WayPoint, Double> longitude() {
			return longitude;
		}

		public static Function<WayPoint, Long> time() {
			return time;
		}
	}

	public static class LinkedWayPoints {

		private static final Function<LinkedTrackPoint, Link> link = new Function<LinkedTrackPoint, Link>() {
			public Link apply(LinkedTrackPoint wayPoint) {
				return wayPoint.getLink();
			}
		};

		private static final Function<LinkedTrackPoint, Integer> elevationDifference = new Function<LinkedTrackPoint, Integer>() {
			public Integer apply(LinkedTrackPoint wayPoint) {
				return Integer.valueOf(wayPoint.getLink()
						.getElevationDifference());
			}
		};

		private static final Function<LinkedTrackPoint, Speed> speed = compose(
				Links.speed(), LinkedWayPoints.link());

		private LinkedWayPoints() {
			super();
		}

		public static Function<LinkedTrackPoint, Link> link() {
			return link;
		}

		public static Function<LinkedTrackPoint, Integer> elevationDifference() {
			return elevationDifference;
		}

		public static Function<LinkedTrackPoint, Speed> speed() {
			return speed;
		}

	}

	public static class Links {

		private static final Function<Link, Speed> speed = new Function<Link, Speed>() {
			public Speed apply(Link link) {
				return link.getSpeed();
			}
		};

		private static final Function<Link, Duration> duration = new Function<Link, Duration>() {
			public Duration apply(Link link) {
				return link.getDuration();
			}
		};

		private static final Function<Link, Distance> distance = new Function<Link, Distance>() {
			public Distance apply(Link link) {
				return link.getDistance();
			}
		};

		public static Function<Link, Speed> speed() {
			return speed;
		}

		public static Function<Link, Duration> duration() {
			return duration;
		}

		public static Function<Link, Distance> distance() {
			return distance;
		}

		public final static Function<LinkedTrackPoint, Speed> speedOfLink = compose(
				speed(), Functions.LinkedWayPoints.link());

	}

	public static class Speeds {

		private static final Function<Speed, Double> value = new Function<Speed, Double>() {

			public Double apply(Speed speed) {
				return Double.valueOf(speed.getValue(speed.getDistanceUnit(),
						speed.getTimeUnit()));
			}
		};

		private Speeds() {
			super();
		}

		public static Function<Speed, Double> value() {
			return value;
		}

		public static Function<Speed, Speed> convert(
				final DistanceUnit distanceUnit, final TimeUnit timeUnit) {
			return new Function<Speed, Speed>() {
				public Speed apply(Speed speed) {
					return speed.convert(distanceUnit, timeUnit);
				}
			};
		}

	}

	public static class Distances {

		public static Function<Distance, DistanceUnit> distanceUnit = new Function<Distance, DistanceUnit>() {
			@Override
			public DistanceUnit apply(Distance distance) {
				return distance.getDistanceUnit();
			}
		};

	}

	public static class Gradients {

		public static Function<Gradient, DistanceUnit> distanceUnit = new Function<Gradient, DistanceUnit>() {
			@Override
			public DistanceUnit apply(Gradient gradient) {
				return gradient.getDistanceUnit();
			}
		};

	}

	public static class Collections {

		private static Function<? extends Collection<?>, ? extends Object> get0 = get(0);

		public static <T> Function<Collection<? extends T>, T> get(final int idx) {
			return new Function<Collection<? extends T>, T>() {
				@Override
				public T apply(Collection<? extends T> collection) {
					return Iterables.get(collection, idx);
				}
			};
		}

		@SuppressWarnings("unchecked")
		public static <T> Function<Collection<? extends T>, T> get0() {
			return (Function<Collection<? extends T>, T>) get0;
		}

	}

}

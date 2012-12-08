package be.emich.labs.villohelper.util;

import java.text.DecimalFormat;

import android.widget.LinearLayout.LayoutParams;

public class Util {
	/*
	 * ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * ::
	 */
	/* :: : */
	/* :: This routine calculates the distance between two points (given the : */
	/* :: latitude/longitude of those points). It is being used to calculate : */
	/* :: the distance between two ZIP Codes or Postal Codes using our : */
	/* :: ZIPCodeWorld(TM) and PostalCodeWorld(TM) products. : */
	/* :: : */
	/* :: Definitions: : */
	/* :: South latitudes are negative, east longitudes are positive : */
	/* :: : */
	/* :: Passed to function: : */
	/* :: lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees) : */
	/* :: lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees) : */
	/* :: unit = the unit you desire for results : */
	/* :: where: 'M' is statute miles : */
	/* :: 'K' is kilometers (default) : */
	/* :: 'N' is nautical miles : */
	/*
	 * :: United States ZIP Code/ Canadian Postal Code databases with latitude &
	 * :
	 */
	/* :: longitude are available at http://www.zipcodeworld.com : */
	/* :: : */
	/* :: For enquiries, please contact sales@zipcodeworld.com : */
	/* :: : */
	/* :: Official Web site: http://www.zipcodeworld.com : */
	/* :: : */
	/* :: Hexa Software Development Center © All Rights Reserved 2004 : */
	/* :: : */
	/*
	 * ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	 * ::
	 */

	public static double distance(double lat1, double lon1, double lat2,
			double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		// Log.v("STIBLocation","Distance: "+lat1+" "+lon1+" "+lat2+" "+lon2+" "+dist);
		return (dist);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	public static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public static final LayoutParams WRAPPED = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	public static final LayoutParams FILL_BOTH = new LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	public static final LayoutParams FILL_HORIZONTAL = new LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	public static final LayoutParams FILL_VERTICAL = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);

	public static double calculateHypothenuse(double x1, double y1, double x2,
			double y2) {
		double l1 = x2 - x1;
		double l2 = y2 - y1;

		return Math.sqrt((l1 * l1) + (l2 * l2));
	}

	public static double calculateAngle(double x1, double y1, double x2,
			double y2) {
		double dx = x2 - x1;
		double dy = y2 - y1;

		return (Math.atan2(dx, dy) * 180) / Math.PI;

	}

	public static String convertKilometerDistanceToString(Double distance) {
		if (distance > 1) {
			DecimalFormat df = new DecimalFormat("#.#");
			return "±" + df.format(distance) + "km";
		} else {
			return "±" + Math.round(distance * 1000) + "m";
		}
	}
}

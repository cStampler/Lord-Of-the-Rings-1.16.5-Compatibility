package lotr.common.fac;

public class AreaBorders {
	public final double xMin;
	public final double xMax;
	public final double zMin;
	public final double zMax;

	public AreaBorders(double xMin, double xMax, double zMin, double zMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.zMin = zMin;
		this.zMax = zMax;
	}

	public double getHeight() {
		return zMax - zMin;
	}

	public double getWidth() {
		return xMax - xMin;
	}

	public double getXCentre() {
		return (xMin + xMax) / 2.0D;
	}

	public double getXMax() {
		return xMax;
	}

	public double getXMin() {
		return xMin;
	}

	public double getZCentre() {
		return (zMin + zMax) / 2.0D;
	}

	public double getZMax() {
		return zMax;
	}

	public double getZMin() {
		return zMin;
	}
}

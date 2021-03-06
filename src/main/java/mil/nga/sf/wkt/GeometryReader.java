package mil.nga.sf.wkt;

import java.io.IOException;
import java.util.Locale;

import mil.nga.sf.CircularString;
import mil.nga.sf.CompoundCurve;
import mil.nga.sf.Curve;
import mil.nga.sf.CurvePolygon;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryCollection;
import mil.nga.sf.GeometryType;
import mil.nga.sf.LineString;
import mil.nga.sf.MultiLineString;
import mil.nga.sf.MultiPoint;
import mil.nga.sf.MultiPolygon;
import mil.nga.sf.Point;
import mil.nga.sf.Polygon;
import mil.nga.sf.PolyhedralSurface;
import mil.nga.sf.Surface;
import mil.nga.sf.TIN;
import mil.nga.sf.Triangle;
import mil.nga.sf.util.SFException;
import mil.nga.sf.util.TextReader;
import mil.nga.sf.util.filter.GeometryFilter;

/**
 * Well Known Text reader
 * 
 * @author osbornb
 */
public class GeometryReader {

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param text
	 *            well-known text
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Geometry readGeometry(String text) throws IOException {
		return readGeometry(text, null, null);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param text
	 *            well-known text
	 * @param filter
	 *            geometry filter
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Geometry readGeometry(String text, GeometryFilter filter)
			throws IOException {
		return readGeometry(text, filter, null);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param text
	 *            well-known text
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static <T extends Geometry> T readGeometry(String text,
			Class<T> expectedType) throws IOException {
		return readGeometry(text, null, expectedType);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param text
	 *            well-known text
	 * @param filter
	 *            geometry filter
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static <T extends Geometry> T readGeometry(String text,
			GeometryFilter filter, Class<T> expectedType) throws IOException {
		T geometry = null;
		GeometryReader reader = new GeometryReader(text);
		try {
			geometry = reader.read(filter, expectedType);
		} finally {
			reader.close();
		}
		return geometry;
	}

	/**
	 * Text Reader
	 */
	private TextReader reader;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            well-known text
	 * @since 1.0.1
	 */
	public GeometryReader(String text) {
		this(new TextReader(text));
	}

	/**
	 * Constructor
	 * 
	 * @param reader
	 *            text reader
	 * @since 1.0.1
	 */
	public GeometryReader(TextReader reader) {
		this.reader = reader;
	}

	/**
	 * Get the text reader
	 * 
	 * @return text reader
	 * @since 1.0.1
	 */
	public TextReader getTextReader() {
		return reader;
	}

	/**
	 * Close the text reader
	 * 
	 * @since 1.0.1
	 */
	public void close() {
		reader.close();
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Geometry read() throws IOException {
		return read(null, null);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param filter
	 *            geometry filter
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Geometry read(GeometryFilter filter) throws IOException {
		return read(filter, null);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public <T extends Geometry> T read(Class<T> expectedType)
			throws IOException {
		return read(null, expectedType);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param filter
	 *            geometry filter
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public <T extends Geometry> T read(GeometryFilter filter,
			Class<T> expectedType) throws IOException {
		return read(filter, null, expectedType);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param filter
	 *            geometry filter
	 * @param containingType
	 *            containing geometry type
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public <T extends Geometry> T read(GeometryFilter filter,
			GeometryType containingType, Class<T> expectedType)
			throws IOException {

		Geometry geometry = null;

		// Read the geometry type
		GeometryTypeInfo geometryTypeInfo = readGeometryType();

		if (geometryTypeInfo != null) {

			GeometryType geometryType = geometryTypeInfo.getGeometryType();
			boolean hasZ = geometryTypeInfo.hasZ();
			boolean hasM = geometryTypeInfo.hasM();

			switch (geometryType) {

			case GEOMETRY:
				throw new SFException("Unexpected Geometry Type of "
						+ geometryType.name() + " which is abstract");
			case POINT:
				geometry = readPointText(hasZ, hasM);
				break;
			case LINESTRING:
				geometry = readLineString(filter, hasZ, hasM);
				break;
			case POLYGON:
				geometry = readPolygon(filter, hasZ, hasM);
				break;
			case MULTIPOINT:
				geometry = readMultiPoint(filter, hasZ, hasM);
				break;
			case MULTILINESTRING:
				geometry = readMultiLineString(filter, hasZ, hasM);
				break;
			case MULTIPOLYGON:
				geometry = readMultiPolygon(filter, hasZ, hasM);
				break;
			case GEOMETRYCOLLECTION:
				geometry = readGeometryCollection(filter, hasZ, hasM);
				break;
			case MULTICURVE:
				geometry = readMultiCurve(filter, hasZ, hasM);
				break;
			case MULTISURFACE:
				geometry = readMultiSurface(filter, hasZ, hasM);
				break;
			case CIRCULARSTRING:
				geometry = readCircularString(filter, hasZ, hasM);
				break;
			case COMPOUNDCURVE:
				geometry = readCompoundCurve(filter, hasZ, hasM);
				break;
			case CURVEPOLYGON:
				geometry = readCurvePolygon(filter, hasZ, hasM);
				break;
			case CURVE:
				throw new SFException("Unexpected Geometry Type of "
						+ geometryType.name() + " which is abstract");
			case SURFACE:
				throw new SFException("Unexpected Geometry Type of "
						+ geometryType.name() + " which is abstract");
			case POLYHEDRALSURFACE:
				geometry = readPolyhedralSurface(filter, hasZ, hasM);
				break;
			case TIN:
				geometry = readTIN(filter, hasZ, hasM);
				break;
			case TRIANGLE:
				geometry = readTriangle(filter, hasZ, hasM);
				break;
			default:
				throw new SFException(
						"Geometry Type not supported: " + geometryType);
			}

			if (!filter(filter, containingType, geometry)) {
				geometry = null;
			}

			// If there is an expected type, verify the geometry is of that type
			if (expectedType != null && geometry != null
					&& !expectedType.isAssignableFrom(geometry.getClass())) {
				throw new SFException("Unexpected Geometry Type. Expected: "
						+ expectedType.getSimpleName() + ", Actual: "
						+ geometry.getClass().getSimpleName());
			}

		}

		@SuppressWarnings("unchecked")
		T result = (T) geometry;

		return result;
	}

	/**
	 * Read the geometry type info
	 * 
	 * @return geometry type info
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public GeometryTypeInfo readGeometryType() throws IOException {

		GeometryTypeInfo geometryInfo = null;

		// Read the geometry type
		String geometryTypeValue = reader.readToken();

		if (geometryTypeValue != null
				&& !geometryTypeValue.equalsIgnoreCase("EMPTY")) {

			boolean hasZ = false;
			boolean hasM = false;

			// Determine the geometry type
			GeometryType geometryType = GeometryType
					.findName(geometryTypeValue);

			// If not found, check if the geometry type has Z and/or M suffix
			if (geometryType == null) {

				// Check if the Z and/or M is appended to the geometry type
				String geomType = geometryTypeValue.toUpperCase(Locale.US);
				if (geomType.endsWith("Z")) {
					hasZ = true;
				} else if (geomType.endsWith("M")) {
					hasM = true;
					if (geomType.endsWith("ZM")) {
						hasZ = true;
					}
				}

				int suffixSize = 0;
				if (hasZ) {
					suffixSize++;
				}
				if (hasM) {
					suffixSize++;
				}

				if (suffixSize > 0) {
					// Check for the geometry type without the suffix
					geomType = geometryTypeValue.substring(0,
							geometryTypeValue.length() - suffixSize);
					geometryType = GeometryType.findName(geomType);
				}

				if (geometryType == null) {
					throw new SFException(
							"Expected a valid geometry type, found: '"
									+ geometryTypeValue + "'");

				}

			}

			// Determine if the geometry has a z (3d) or m (linear referencing
			// system) value
			if (!hasZ && !hasM) {

				// Peek at the next token without popping it off
				String next = reader.peekToken();

				switch (toUpperCase(next)) {
				case "Z":
					hasZ = true;
					break;
				case "M":
					hasM = true;
					break;
				case "ZM":
					hasZ = true;
					hasM = true;
					break;
				case "(":
				case "EMPTY":
					break;
				default:
					throw new SFException(
							"Invalid value following geometry type: '"
									+ geometryTypeValue + "', value: '" + next
									+ "'");
				}

				if (hasZ || hasM) {
					// Read off the Z and/or M token
					reader.readToken();
				}

			}

			geometryInfo = new GeometryTypeInfo(geometryType, hasZ, hasM);

		}

		return geometryInfo;
	}

	/**
	 * Read a Point
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return point
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Point readPointText(boolean hasZ, boolean hasM) throws IOException {

		Point point = null;

		if (leftParenthesisOrEmpty(reader)) {
			point = readPoint(hasZ, hasM);
			rightParenthesis(reader);
		}

		return point;
	}

	/**
	 * Read a Point
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return point
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Point readPoint(boolean hasZ, boolean hasM) throws IOException {

		double x = reader.readDouble();
		double y = reader.readDouble();

		Point point = new Point(hasZ, hasM, x, y);

		if (hasZ || hasM) {
			if (hasZ) {
				point.setZ(reader.readDouble());
			}

			if (hasM) {
				point.setM(reader.readDouble());
			}
		} else if (!isCommaOrRightParenthesis(reader)) {

			point.setZ(reader.readDouble());

			if (!isCommaOrRightParenthesis(reader)) {

				point.setM(reader.readDouble());

			}

		}

		return point;
	}

	/**
	 * Read a Line String
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return line string
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public LineString readLineString(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readLineString(filter, hasZ, hasM);
	}

	/**
	 * Read a Line String
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return line string
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public LineString readLineString(GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {

		LineString lineString = null;

		if (leftParenthesisOrEmpty(reader)) {

			lineString = new LineString(hasZ, hasM);

			do {
				Point point = readPoint(hasZ, hasM);
				if (filter(filter, GeometryType.LINESTRING, point)) {
					lineString.addPoint(point);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return lineString;
	}

	/**
	 * Read a Polygon
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polygon
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Polygon readPolygon(boolean hasZ, boolean hasM) throws IOException {
		GeometryFilter filter = null;
		return readPolygon(filter, hasZ, hasM);
	}

	/**
	 * Read a Polygon
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polygon
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Polygon readPolygon(GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {

		Polygon polygon = null;

		if (leftParenthesisOrEmpty(reader)) {

			polygon = new Polygon(hasZ, hasM);

			do {
				LineString ring = readLineString(filter, hasZ, hasM);
				if (filter(filter, GeometryType.POLYGON, ring)) {
					polygon.addRing(ring);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return polygon;
	}

	/**
	 * Read a Multi Point
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi point
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public MultiPoint readMultiPoint(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readMultiPoint(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Point
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi point
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public MultiPoint readMultiPoint(GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {

		MultiPoint multiPoint = null;

		if (leftParenthesisOrEmpty(reader)) {

			multiPoint = new MultiPoint(hasZ, hasM);

			do {
				Point point = null;
				if (isLeftParenthesisOrEmpty(reader)) {
					point = readPointText(hasZ, hasM);
				} else {
					point = readPoint(hasZ, hasM);
				}
				if (filter(filter, GeometryType.MULTIPOINT, point)) {
					multiPoint.addPoint(point);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return multiPoint;
	}

	/**
	 * Read a Multi Line String
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi line string
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public MultiLineString readMultiLineString(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readMultiLineString(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Line String
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi line string
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public MultiLineString readMultiLineString(GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {

		MultiLineString multiLineString = null;

		if (leftParenthesisOrEmpty(reader)) {

			multiLineString = new MultiLineString(hasZ, hasM);

			do {
				LineString lineString = readLineString(filter, hasZ, hasM);
				if (filter(filter, GeometryType.MULTILINESTRING, lineString)) {
					multiLineString.addLineString(lineString);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return multiLineString;
	}

	/**
	 * Read a Multi Polygon
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi polygon
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public MultiPolygon readMultiPolygon(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readMultiPolygon(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Polygon
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi polygon
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public MultiPolygon readMultiPolygon(GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {

		MultiPolygon multiPolygon = null;

		if (leftParenthesisOrEmpty(reader)) {

			multiPolygon = new MultiPolygon(hasZ, hasM);

			do {
				Polygon polygon = readPolygon(filter, hasZ, hasM);
				if (filter(filter, GeometryType.MULTIPOLYGON, polygon)) {
					multiPolygon.addPolygon(polygon);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return multiPolygon;
	}

	/**
	 * Read a Geometry Collection
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return geometry collection
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public GeometryCollection<Geometry> readGeometryCollection(boolean hasZ,
			boolean hasM) throws IOException {
		GeometryFilter filter = null;
		return readGeometryCollection(filter, hasZ, hasM);
	}

	/**
	 * Read a Geometry Collection
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return geometry collection
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public GeometryCollection<Geometry> readGeometryCollection(
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {

		GeometryCollection<Geometry> geometryCollection = null;

		if (leftParenthesisOrEmpty(reader)) {

			geometryCollection = new GeometryCollection<Geometry>(hasZ, hasM);

			do {
				Geometry geometry = read(filter,
						GeometryType.GEOMETRYCOLLECTION, Geometry.class);
				if (geometry != null) {
					geometryCollection.addGeometry(geometry);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return geometryCollection;
	}

	/**
	 * Read a Multi Curve
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi curve
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public GeometryCollection<Curve> readMultiCurve(GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {

		GeometryCollection<Curve> multiCurve = null;

		if (leftParenthesisOrEmpty(reader)) {

			multiCurve = new GeometryCollection<Curve>(hasZ, hasM);

			do {
				Curve curve = null;
				if (isLeftParenthesisOrEmpty(reader)) {
					curve = readLineString(filter, hasZ, hasM);
					if (!filter(filter, GeometryType.MULTICURVE, curve)) {
						curve = null;
					}
				} else {
					curve = read(filter, GeometryType.MULTICURVE, Curve.class);
				}
				if (curve != null) {
					multiCurve.addGeometry(curve);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return multiCurve;
	}

	/**
	 * Read a Multi Surface
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi surface
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public GeometryCollection<Surface> readMultiSurface(GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {

		GeometryCollection<Surface> multiSurface = null;

		if (leftParenthesisOrEmpty(reader)) {

			multiSurface = new GeometryCollection<Surface>(hasZ, hasM);

			do {
				Surface surface = null;
				if (isLeftParenthesisOrEmpty(reader)) {
					surface = readPolygon(filter, hasZ, hasM);
					if (!filter(filter, GeometryType.MULTISURFACE, surface)) {
						surface = null;
					}
				} else {
					surface = read(filter, GeometryType.MULTISURFACE,
							Surface.class);
				}
				if (surface != null) {
					multiSurface.addGeometry(surface);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return multiSurface;
	}

	/**
	 * Read a Circular String
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return circular string
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public CircularString readCircularString(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readCircularString(filter, hasZ, hasM);
	}

	/**
	 * Read a Circular String
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return circular string
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public CircularString readCircularString(GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {

		CircularString circularString = null;

		if (leftParenthesisOrEmpty(reader)) {

			circularString = new CircularString(hasZ, hasM);

			do {
				Point point = readPoint(hasZ, hasM);
				if (filter(filter, GeometryType.CIRCULARSTRING, point)) {
					circularString.addPoint(point);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return circularString;
	}

	/**
	 * Read a Compound Curve
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return compound curve
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public CompoundCurve readCompoundCurve(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readCompoundCurve(filter, hasZ, hasM);
	}

	/**
	 * Read a Compound Curve
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return compound curve
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public CompoundCurve readCompoundCurve(GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {

		CompoundCurve compoundCurve = null;

		if (leftParenthesisOrEmpty(reader)) {

			compoundCurve = new CompoundCurve(hasZ, hasM);

			do {
				LineString lineString = null;
				if (isLeftParenthesisOrEmpty(reader)) {
					lineString = readLineString(filter, hasZ, hasM);
					if (!filter(filter, GeometryType.COMPOUNDCURVE,
							lineString)) {
						lineString = null;
					}
				} else {
					lineString = read(filter, GeometryType.COMPOUNDCURVE,
							LineString.class);
				}
				if (lineString != null) {
					compoundCurve.addLineString(lineString);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return compoundCurve;
	}

	/**
	 * Read a Curve Polygon
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return curve polygon
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public CurvePolygon<Curve> readCurvePolygon(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readCurvePolygon(filter, hasZ, hasM);
	}

	/**
	 * Read a Curve Polygon
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return curve polygon
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public CurvePolygon<Curve> readCurvePolygon(GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {

		CurvePolygon<Curve> curvePolygon = null;

		if (leftParenthesisOrEmpty(reader)) {

			curvePolygon = new CurvePolygon<Curve>(hasZ, hasM);

			do {
				Curve ring = null;
				if (isLeftParenthesisOrEmpty(reader)) {
					ring = readLineString(filter, hasZ, hasM);
					if (!filter(filter, GeometryType.CURVEPOLYGON, ring)) {
						ring = null;
					}
				} else {
					ring = read(filter, GeometryType.CURVEPOLYGON, Curve.class);
				}
				if (ring != null) {
					curvePolygon.addRing(ring);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return curvePolygon;
	}

	/**
	 * Read a Polyhedral Surface
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polyhedral surface
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public PolyhedralSurface readPolyhedralSurface(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readPolyhedralSurface(filter, hasZ, hasM);
	}

	/**
	 * Read a Polyhedral Surface
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polyhedral surface
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public PolyhedralSurface readPolyhedralSurface(GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {

		PolyhedralSurface polyhedralSurface = null;

		if (leftParenthesisOrEmpty(reader)) {

			polyhedralSurface = new PolyhedralSurface(hasZ, hasM);

			do {
				Polygon polygon = readPolygon(filter, hasZ, hasM);
				if (filter(filter, GeometryType.POLYHEDRALSURFACE, polygon)) {
					polyhedralSurface.addPolygon(polygon);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return polyhedralSurface;
	}

	/**
	 * Read a TIN
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return TIN
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public TIN readTIN(boolean hasZ, boolean hasM) throws IOException {
		GeometryFilter filter = null;
		return readTIN(filter, hasZ, hasM);
	}

	/**
	 * Read a TIN
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return TIN
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public TIN readTIN(GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {

		TIN tin = null;

		if (leftParenthesisOrEmpty(reader)) {

			tin = new TIN(hasZ, hasM);

			do {
				Polygon polygon = readPolygon(filter, hasZ, hasM);
				if (filter(filter, GeometryType.TIN, polygon)) {
					tin.addPolygon(polygon);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return tin;
	}

	/**
	 * Read a Triangle
	 * 
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return triangle
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Triangle readTriangle(boolean hasZ, boolean hasM)
			throws IOException {
		GeometryFilter filter = null;
		return readTriangle(filter, hasZ, hasM);
	}

	/**
	 * Read a Triangle
	 * 
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return triangle
	 * @throws IOException
	 *             upon failure to read
	 * @since 1.0.1
	 */
	public Triangle readTriangle(GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {

		Triangle triangle = null;

		if (leftParenthesisOrEmpty(reader)) {

			triangle = new Triangle(hasZ, hasM);

			do {
				LineString ring = readLineString(filter, hasZ, hasM);
				if (filter(filter, GeometryType.TRIANGLE, ring)) {
					triangle.addRing(ring);
				}
			} while (commaOrRightParenthesis(reader));

		}

		return triangle;
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param reader
	 *            text reader
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Geometry readGeometry(TextReader reader) throws IOException {
		return readGeometry(reader, null, null);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Geometry readGeometry(TextReader reader,
			GeometryFilter filter) throws IOException {
		return readGeometry(reader, filter, null);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param reader
	 *            text reader
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static <T extends Geometry> T readGeometry(TextReader reader,
			Class<T> expectedType) throws IOException {
		return readGeometry(reader, null, expectedType);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static <T extends Geometry> T readGeometry(TextReader reader,
			GeometryFilter filter, Class<T> expectedType) throws IOException {
		return readGeometry(reader, filter, null, expectedType);
	}

	/**
	 * Read a geometry from the well-known text
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param containingType
	 *            containing geometry type
	 * @param expectedType
	 *            expected type
	 * @param <T>
	 *            geometry type
	 * @return geometry
	 * @throws IOException
	 *             upon failure to read
	 */
	public static <T extends Geometry> T readGeometry(TextReader reader,
			GeometryFilter filter, GeometryType containingType,
			Class<T> expectedType) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.read(filter, containingType, expectedType);
	}

	/**
	 * Read the geometry type info
	 * 
	 * @param reader
	 *            text reader
	 * @return geometry type info
	 * @throws IOException
	 *             upon failure to read
	 */
	public static GeometryTypeInfo readGeometryType(TextReader reader)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readGeometryType();
	}

	/**
	 * Read a Point
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return point
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Point readPointText(TextReader reader, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readPointText(hasZ, hasM);
	}

	/**
	 * Read a Point
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return point
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Point readPoint(TextReader reader, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readPoint(hasZ, hasM);
	}

	/**
	 * Read a Line String
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return line string
	 * @throws IOException
	 *             upon failure to read
	 */
	public static LineString readLineString(TextReader reader, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readLineString(hasZ, hasM);
	}

	/**
	 * Read a Line String
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return line string
	 * @throws IOException
	 *             upon failure to read
	 */
	public static LineString readLineString(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readLineString(filter, hasZ, hasM);
	}

	/**
	 * Read a Polygon
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polygon
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Polygon readPolygon(TextReader reader, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readPolygon(hasZ, hasM);
	}

	/**
	 * Read a Polygon
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polygon
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Polygon readPolygon(TextReader reader, GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readPolygon(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Point
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi point
	 * @throws IOException
	 *             upon failure to read
	 */
	public static MultiPoint readMultiPoint(TextReader reader, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiPoint(hasZ, hasM);
	}

	/**
	 * Read a Multi Point
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi point
	 * @throws IOException
	 *             upon failure to read
	 */
	public static MultiPoint readMultiPoint(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiPoint(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Line String
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi line string
	 * @throws IOException
	 *             upon failure to read
	 */
	public static MultiLineString readMultiLineString(TextReader reader,
			boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiLineString(hasZ, hasM);
	}

	/**
	 * Read a Multi Line String
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi line string
	 * @throws IOException
	 *             upon failure to read
	 */
	public static MultiLineString readMultiLineString(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiLineString(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Polygon
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi polygon
	 * @throws IOException
	 *             upon failure to read
	 */
	public static MultiPolygon readMultiPolygon(TextReader reader, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiPolygon(hasZ, hasM);
	}

	/**
	 * Read a Multi Polygon
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi polygon
	 * @throws IOException
	 *             upon failure to read
	 */
	public static MultiPolygon readMultiPolygon(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiPolygon(filter, hasZ, hasM);
	}

	/**
	 * Read a Geometry Collection
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return geometry collection
	 * @throws IOException
	 *             upon failure to read
	 */
	public static GeometryCollection<Geometry> readGeometryCollection(
			TextReader reader, boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readGeometryCollection(hasZ, hasM);
	}

	/**
	 * Read a Geometry Collection
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return geometry collection
	 * @throws IOException
	 *             upon failure to read
	 */
	public static GeometryCollection<Geometry> readGeometryCollection(
			TextReader reader, GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readGeometryCollection(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Curve
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi curve
	 * @throws IOException
	 *             upon failure to read
	 */
	public static GeometryCollection<Curve> readMultiCurve(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiCurve(filter, hasZ, hasM);
	}

	/**
	 * Read a Multi Surface
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return multi surface
	 * @throws IOException
	 *             upon failure to read
	 */
	public static GeometryCollection<Surface> readMultiSurface(
			TextReader reader, GeometryFilter filter, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readMultiSurface(filter, hasZ, hasM);
	}

	/**
	 * Read a Circular String
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return circular string
	 * @throws IOException
	 *             upon failure to read
	 */
	public static CircularString readCircularString(TextReader reader,
			boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readCircularString(hasZ, hasM);
	}

	/**
	 * Read a Circular String
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return circular string
	 * @throws IOException
	 *             upon failure to read
	 */
	public static CircularString readCircularString(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readCircularString(filter, hasZ, hasM);
	}

	/**
	 * Read a Compound Curve
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return compound curve
	 * @throws IOException
	 *             upon failure to read
	 */
	public static CompoundCurve readCompoundCurve(TextReader reader,
			boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readCompoundCurve(hasZ, hasM);
	}

	/**
	 * Read a Compound Curve
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return compound curve
	 * @throws IOException
	 *             upon failure to read
	 */
	public static CompoundCurve readCompoundCurve(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readCompoundCurve(filter, hasZ, hasM);
	}

	/**
	 * Read a Curve Polygon
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return curve polygon
	 * @throws IOException
	 *             upon failure to read
	 */
	public static CurvePolygon<Curve> readCurvePolygon(TextReader reader,
			boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readCurvePolygon(hasZ, hasM);
	}

	/**
	 * Read a Curve Polygon
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return curve polygon
	 * @throws IOException
	 *             upon failure to read
	 */
	public static CurvePolygon<Curve> readCurvePolygon(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readCurvePolygon(filter, hasZ, hasM);
	}

	/**
	 * Read a Polyhedral Surface
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polyhedral surface
	 * @throws IOException
	 *             upon failure to read
	 */
	public static PolyhedralSurface readPolyhedralSurface(TextReader reader,
			boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readPolyhedralSurface(hasZ, hasM);
	}

	/**
	 * Read a Polyhedral Surface
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return polyhedral surface
	 * @throws IOException
	 *             upon failure to read
	 */
	public static PolyhedralSurface readPolyhedralSurface(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readPolyhedralSurface(filter, hasZ, hasM);
	}

	/**
	 * Read a TIN
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return TIN
	 * @throws IOException
	 *             upon failure to read
	 */
	public static TIN readTIN(TextReader reader, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readTIN(hasZ, hasM);
	}

	/**
	 * Read a TIN
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return TIN
	 * @throws IOException
	 *             upon failure to read
	 */
	public static TIN readTIN(TextReader reader, GeometryFilter filter,
			boolean hasZ, boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readTIN(filter, hasZ, hasM);
	}

	/**
	 * Read a Triangle
	 * 
	 * @param reader
	 *            text reader
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return triangle
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Triangle readTriangle(TextReader reader, boolean hasZ,
			boolean hasM) throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readTriangle(hasZ, hasM);
	}

	/**
	 * Read a Triangle
	 * 
	 * @param reader
	 *            text reader
	 * @param filter
	 *            geometry filter
	 * @param hasZ
	 *            has z flag
	 * @param hasM
	 *            has m flag
	 * @return triangle
	 * @throws IOException
	 *             upon failure to read
	 */
	public static Triangle readTriangle(TextReader reader,
			GeometryFilter filter, boolean hasZ, boolean hasM)
			throws IOException {
		GeometryReader geometryReader = new GeometryReader(reader);
		return geometryReader.readTriangle(filter, hasZ, hasM);
	}

	/**
	 * Read a left parenthesis or empty set
	 * 
	 * @param reader
	 *            text reader
	 * @return true if not empty
	 * @throws IOException
	 *             upon failure to read
	 */
	private static boolean leftParenthesisOrEmpty(TextReader reader)
			throws IOException {

		boolean nonEmpty;

		String token = reader.readToken();

		switch (toUpperCase(token)) {
		case "EMPTY":
			nonEmpty = false;
			break;
		case "(":
			nonEmpty = true;
			break;
		default:
			throw new SFException(
					"Invalid token, expected 'EMPTY' or '('. found: '" + token
							+ "'");
		}

		return nonEmpty;
	}

	/**
	 * Read a comma or right parenthesis
	 * 
	 * @param reader
	 *            text reader
	 * @return true if a comma
	 * @throws IOException
	 *             upon failure to read
	 */
	private static boolean commaOrRightParenthesis(TextReader reader)
			throws IOException {

		boolean comma;

		String token = reader.readToken();

		switch (toUpperCase(token)) {
		case ",":
			comma = true;
			break;
		case ")":
			comma = false;
			break;
		default:
			throw new SFException("Invalid token, expected ',' or ')'. found: '"
					+ token + "'");
		}

		return comma;
	}

	/**
	 * Read a right parenthesis
	 * 
	 * @param reader
	 *            text reader
	 * @throws IOException
	 *             upon failure to read
	 */
	private static void rightParenthesis(TextReader reader) throws IOException {
		String token = reader.readToken();
		if (!token.equals(")")) {
			throw new SFException(
					"Invalid token, expected ')'. found: '" + token + "'");
		}
	}

	/**
	 * Determine if the next token is either a left parenthesis or empty
	 * 
	 * @param reader
	 *            text reader
	 * @return true if a left parenthesis or empty
	 * @throws IOException
	 *             upon failure to read
	 */
	private static boolean isLeftParenthesisOrEmpty(TextReader reader)
			throws IOException {

		boolean is = false;

		String token = reader.peekToken();

		switch (toUpperCase(token)) {
		case "EMPTY":
		case "(":
			is = true;
			break;
		default:
		}

		return is;
	}

	/**
	 * Determine if the next token is either a comma or right parenthesis
	 * 
	 * @param reader
	 *            text reader
	 * @return true if a comma
	 * @throws IOException
	 *             upon failure to read
	 */
	private static boolean isCommaOrRightParenthesis(TextReader reader)
			throws IOException {

		boolean is = false;

		String token = reader.peekToken();

		switch (token) {
		case ",":
		case ")":
			is = true;
			break;
		default:
		}

		return is;
	}

	/**
	 * Filter the geometry
	 * 
	 * @param filter
	 *            geometry filter or null
	 * @param containingType
	 *            containing geometry type
	 * @param geometry
	 *            geometry or null
	 * @return true if passes filter
	 */
	private static boolean filter(GeometryFilter filter,
			GeometryType containingType, Geometry geometry) {
		return filter == null || geometry == null
				|| filter.filter(containingType, geometry);
	}

	/**
	 * To upper case helper with null handling for switch statements
	 * 
	 * @param value
	 *            string value
	 * @return upper case value or empty string
	 */
	private static String toUpperCase(String value) {
		return value != null ? value.toUpperCase(Locale.US) : "";
	}

}

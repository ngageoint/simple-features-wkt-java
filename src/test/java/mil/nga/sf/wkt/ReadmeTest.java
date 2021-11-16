package mil.nga.sf.wkt;

import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryType;
import mil.nga.sf.Point;
import mil.nga.sf.wkt.GeometryReader;
import mil.nga.sf.wkt.GeometryWriter;

/**
 * README example tests
 * 
 * @author osbornb
 */
public class ReadmeTest {

	/**
	 * Geometry
	 */
	private static final Geometry GEOMETRY = new Point(1.0, 1.0);

	/**
	 * {@link #GEOMETRY} text
	 */
	private static final String TEXT = "POINT (1.0 1.0)";

	/**
	 * Test read
	 * 
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testRead() throws IOException {

		Geometry geometry = testRead(TEXT);

		TestCase.assertEquals(GEOMETRY, geometry);

	}

	/**
	 * Test read
	 * 
	 * @param text
	 *            text
	 * @return geometry
	 * @throws IOException
	 *             upon error
	 */
	private Geometry testRead(String text) throws IOException {

		// String text = ...

		Geometry geometry = GeometryReader.readGeometry(text);
		GeometryType geometryType = geometry.getGeometryType();

		return geometry;
	}

	/**
	 * Test write
	 * 
	 * @throws IOException
	 *             upon error
	 */
	@Test
	public void testWrite() throws IOException {

		String text = testWrite(GEOMETRY);

		WKTTestUtils.compareText(TEXT, text);

	}

	/**
	 * Test write
	 * 
	 * @param geometry
	 *            geometry
	 * @return text
	 * @throws IOException
	 *             upon error
	 */
	private String testWrite(Geometry geometry) throws IOException {

		// Geometry geometry = ...

		String text = GeometryWriter.writeGeometry(geometry);

		return text;
	}

}

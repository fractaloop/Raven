/**
 * 
 */
package raven.game.test;

import junit.framework.Assert;

import org.junit.Test;

import raven.game.RavenMap;
import raven.utils.MapSerializer;

/**
 * @author chester
 *
 */
public class MapSerializerTests {

	private String emptyString = 
	"<RavenMap>\n" +
	"  <walls/>\n" +
	"  <triggerSystem>\n" +
	"    <triggers/>\n" + 
	"  </triggerSystem>\n" +
	"  <spawnPoints/>\n" +
	"  <doors/>\n" +
	"  <navGraph>\n" +
	"    <nodes/>\n" +
	"    <edges/>\n" +
	"    <isDigraph>false</isDigraph>\n" +
	"    <nextNodeIndex>0</nextNodeIndex>\n" +
	"  </navGraph>\n" +
	"  <spacePartition>\n" +
	"    <cells/>\n" +
	"    <neighbors/>\n" +
	"    <spaceWidth>0.0</spaceWidth>\n" +
	"    <spaceHeight>0.0</spaceHeight>\n" +
	"    <numCellsX>0</numCellsX>\n" +
	"    <numCellsY>0</numCellsY>\n" +
	"    <cellSizeX>NaN</cellSizeX>\n" +
	"    <cellSizeY>NaN</cellSizeY>\n" +
	"  </spacePartition>\n" +
	"  <cellSpaceNeighborhoodRange>0.0</cellSpaceNeighborhoodRange>\n" + 
	"  <sizeX>0</sizeX>\n" +
	"  <sizeY>0</sizeY>\n" +
	"</RavenMap>";
	
	/*
	 * Create an empty Map and deserialize it.
	 */
	@Test
	public void DeserializeEmptyMap() {	
		RavenMap writeMe = CreateEmptyMap();
		String emptyMap = MapSerializer.serializeMap(writeMe);
		Assert.assertEquals(emptyString, emptyMap);
	}
	
	@Test
	public void SerializedEmptyMapEqualsNewEmptyMap(){
		RavenMap expected = CreateEmptyMap();
		RavenMap actual = MapSerializer.deserializeMapFromXML(emptyString);
		Assert.assertEquals(expected, actual);
	}
	
	private RavenMap CreateEmptyMap() {
		return new RavenMap();
	}
}

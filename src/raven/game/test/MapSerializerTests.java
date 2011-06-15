/**
 * 
 */
package raven.game.test;

import junit.framework.Assert;

import org.junit.Ignore;
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
	"  <triggerSystem/>\n" +
	"  <spawnPoints/>\n" +
	"  <doors/>\n" +
	"  <navGraph>\n" +
	"    <nodes/>\n" +
	"    <edges/>\n" +
	"    <isDigraph>false</isDigraph>\n" +
	"    <nextNodeIndex>0</nextNodeIndex>\n" +
	"  </navGraph>\n" +
	"  <sizeX>500</sizeX>\n" +
	"  <sizeY>500</sizeY>\n" +
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
	@Ignore
	public void SerializedEmptyMapEqualsNewEmptyMap(){
		RavenMap expected = CreateEmptyMap();
		RavenMap actual = MapSerializer.deserializeMapFromXML(emptyString);
		Assert.assertEquals(expected, actual);
	}
	
	private RavenMap CreateEmptyMap() {
		return new RavenMap();
	}
}

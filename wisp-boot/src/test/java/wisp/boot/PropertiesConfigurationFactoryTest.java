/*
 * (C) Copyright 2017 Kyle F. Downey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wisp.boot;

import org.junit.Test;
import wisp.api.Configuration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Period;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PropertiesConfigurationFactoryTest {
    @Test
    public void canHandleProperties() {
        var path = new File("test.properties").toPath();
        var factory = new PropertiesConfigurationFactory();
        assertTrue(factory.canHandle(path));
    }

    @Test
    public void cannotHandleOtherExtensions() {
        var path = new File("test.conf").toPath();
        var factory = new PropertiesConfigurationFactory();
        assertFalse(factory.canHandle(path));
    }

    @Test
    public void hasPath() throws Exception {
        var config = aTestConfiguration();
        assertTrue(config.hasPath("foo.bar"));
    }

    @Test
    public void getBoolean() throws Exception {
        var config = aTestConfiguration();
        assertTrue(config.getBoolean("foo.boolean"));
    }

    @Test
    public void getInt() throws Exception {
        var config = aTestConfiguration();
        assertEquals(5, config.getInt("foo.int"));
    }

    @Test
    public void getLong() throws Exception {
        var config = aTestConfiguration();
        assertEquals(5_000_000_000L, config.getLong("foo.long"));
    }

    @Test
    public void getDouble() throws Exception {
        var config = aTestConfiguration();
        assertEquals(4.5, config.getDouble("foo.double"), 0.000001);
    }

    @Test
    public void getString() throws Exception {
        var config = aTestConfiguration();
        assertEquals("text", config.getString("foo.baz"));
    }

    @Test
    public void getEnum() throws Exception {
        var config = aTestConfiguration();
        assertEquals(FooBar.BAR, config.getEnum(FooBar.class, "foo.enum"));
    }

    @Test
    public void getDuration() throws Exception {
        var config = aTestConfiguration();
        assertEquals(Duration.parse("P2D"), config.getDuration("foo.duration"));
    }

    @Test
    public void getPeriod() throws Exception {
        var config = aTestConfiguration();
        assertEquals(Period.ofDays(5), config.getPeriod("foo.period"));
    }

    private Configuration aTestConfiguration() throws IOException {
         var factory = new PropertiesConfigurationFactory();
         return factory.parse(getClass().getResourceAsStream("test.properties"));
    }

    public enum FooBar {
        BAR
    }
}
package me.kolek.hub.fix.server;

import me.kolek.fix.FixMessage;
import me.kolek.fix.constants.TagNum;
import me.kolek.fix.serialization.Message;
import me.kolek.fix.serialization.metadata.MessageMetadata;
import me.kolek.fix.util.FixMessageParser;
import me.kolek.fix.util.FixUtil;
import me.kolek.hub.determinant.Determinant;
import me.kolek.hub.determinant.cache.DeterminantCache;
import me.kolek.hub.fix.serialization.cache.FixSerializationCache;
import me.kolek.hub.fix.serialization.determinant.MessageEvaluable;
import me.kolek.hub.fix.serialization.determinant.MessagePropertyCache;
import me.kolek.hub.test.HubUnitTest;
import org.junit.Test;

import javax.jdo.JDOEnhancer;
import javax.jdo.JDOHelper;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class FixSerializationTest extends HubUnitTest {
    private FixSerializationCache serializationCache;
    private DeterminantCache determinantCache;

    public FixSerializationTest() {
        super(FixServerModule.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        serializationCache = getServiceLocator().getService(FixSerializationCache.class);
        determinantCache = getServiceLocator().getService(DeterminantCache.class);
    }

    @Test
    public void testSerialization() throws Exception {
        FixMessage fixMessage = getFixMessageResource("X_SENDR_RECVR_12");

        String beginString = fixMessage.getValue(TagNum.BeginString).orElseThrow(() -> new Exception("no BeginString"));
        String msgType = fixMessage.getValue(TagNum.MsgType).orElseThrow(() -> new Exception("no MsgType"));
        String fixVersion = fixMessage.getValue(TagNum.ApplVerID).map(FixUtil::toBeginString).orElse(beginString);

        MessageMetadata metadata = serializationCache.getMetadata(msgType, fixVersion, null);
        Message message = metadata.deserialize(fixMessage.getTagValues());

        List<Determinant> matches =
                determinantCache.getMatches("FIXDES", new MessageEvaluable(message, new MessagePropertyCache()));
        matches.forEach(System.out::println);
    }

    private static FixMessage getFixMessageResource(String name) throws Exception {
        try (InputStream inputStream = FixSerializationTest.class.getResourceAsStream(name + ".fix")) {
            return new FixMessageParser().parse(inputStream);
        }
    }
}

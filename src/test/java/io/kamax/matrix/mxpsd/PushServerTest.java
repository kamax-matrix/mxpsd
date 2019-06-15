package io.kamax.matrix.mxpsd;

import com.google.gson.JsonArray;
import io.kamax.matrix.json.GsonUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class PushServerTest {

    private static PushServer pushd;

    @BeforeClass
    public static void beforeClass() {
        pushd = new PushServer();
    }

    @Test
    public void sendNotification() {
        JsonArray devices = new JsonArray();
        devices.add(GsonUtil.makeObj("pushkey", System.getenv("MXPSD_TEST_PUSHKEY")));
        pushd.notify(GsonUtil.makeObj("devices", devices));
    }

}

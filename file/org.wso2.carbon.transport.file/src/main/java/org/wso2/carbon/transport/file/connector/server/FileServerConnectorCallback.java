/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.transport.file.connector.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This {@link CarbonCallback} will be called by a message processsor to acknowledge that
 * it has finished processing the file input stream, so it may be closed from the transport end.
 */
public class FileServerConnectorCallback implements CarbonCallback {

    private static final Logger log = LoggerFactory.getLogger(FileServerConnectorCallback.class);
    /**
     * Countdown latch to wait for the acknowledgement from the application layer.
     */
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void done(CarbonMessage carbonMessage) {
        if (log.isDebugEnabled()) {
            log.debug("Message processor acknowledgement received.");
        }
        latch.countDown();
    }

    /**
     * This makes the relevant process to wait till there is a acknowledgement from the application layer.
     *
     * @param timeOutInterval Time-out interval in milliseconds for waiting for the acknowledgement
     * @throws InterruptedException Interrupted Exception.
     */
    protected void waitTillDone(long timeOutInterval) throws InterruptedException {
        boolean isCallbackReceived = latch.await(timeOutInterval, TimeUnit.MILLISECONDS);

        if (!isCallbackReceived) {
            log.warn("The time for waiting the acknowledgement callback exceeded " + timeOutInterval + ". Proceeding "
                    + "to the next polling cycle");
        }
    }
}

package org.corfudb.runtime.clients;

import com.google.common.collect.ImmutableSet;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;
import org.corfudb.protocols.wireprotocol.*;
import org.corfudb.protocols.wireprotocol.LogUnitReadResponseMsg.ReadResult;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Created by mwei on 12/10/15.
 */
public class LogUnitClient implements IClient {
    @Setter
    IClientRouter router;

    /**
     * Handle a incoming message on the channel
     *
     * @param msg The incoming message
     * @param ctx The channel handler context
     */
    @Override
    public void handleMessage(CorfuMsg msg, ChannelHandlerContext ctx) {
        switch (msg.getMsgType())
        {
            case ERROR_OK:
                router.completeRequest(msg.getRequestID(), true);
                break;
            case ERROR_TRIMMED:
                router.completeExceptionally(msg.getRequestID(), new Exception("Trimmed"));
                break;
            case ERROR_OVERWRITE:
                router.completeExceptionally(msg.getRequestID(), new Exception("Overwrite"));
                break;
            case ERROR_OOS:
                router.completeExceptionally(msg.getRequestID(), new Exception("OOS"));
                break;
            case ERROR_RANK:
                router.completeExceptionally(msg.getRequestID(), new Exception("Rank"));
                break;
            case READ_RESPONSE:
                router.completeRequest(msg.getRequestID(), new ReadResult((LogUnitReadResponseMsg)msg));
        }
    }

    /** The messages this client should handle. */
    @Getter
    public final Set<CorfuMsg.NettyCorfuMsgType> HandledTypes =
            new ImmutableSet.Builder<CorfuMsg.NettyCorfuMsgType>()
                    .add(CorfuMsg.NettyCorfuMsgType.WRITE)
                    .add(CorfuMsg.NettyCorfuMsgType.READ_REQUEST)
                    .add(CorfuMsg.NettyCorfuMsgType.READ_RESPONSE)
                    .add(CorfuMsg.NettyCorfuMsgType.TRIM)
                    .add(CorfuMsg.NettyCorfuMsgType.FILL_HOLE)
                    .add(CorfuMsg.NettyCorfuMsgType.FORCE_GC)
                    .add(CorfuMsg.NettyCorfuMsgType.GC_INTERVAL)

                    .add(CorfuMsg.NettyCorfuMsgType.ERROR_OK)
                    .add(CorfuMsg.NettyCorfuMsgType.ERROR_TRIMMED)
                    .add(CorfuMsg.NettyCorfuMsgType.ERROR_OVERWRITE)
                    .add(CorfuMsg.NettyCorfuMsgType.ERROR_OOS)
                    .add(CorfuMsg.NettyCorfuMsgType.ERROR_RANK)
                    .build();

    /**
     * Asynchronously write to the logging unit.
     *
     * @param address     The address to write to.
     * @param streams     The streams, if any, that this write belongs to.
     * @param rank        The rank of this write (used for quorum replication).
     * @param writeObject The object, pre-serialization, to write.
     * @return A CompletableFuture which will complete with the WriteResult once the
     * write completes.
     */
    public CompletableFuture<Boolean> write(long address, Set<UUID> streams, long rank, Object writeObject) {
        LogUnitWriteMsg w = new LogUnitWriteMsg(address);
        w.setStreams(streams);
        w.setRank(rank);
        w.setPayload(writeObject);
        return router.sendMessageAndGetCompletable(w);
    }

    /**
     * Asynchronously read from the logging unit.
     *
     * @param address The address to read from.
     * @return A CompletableFuture which will complete with a ReadResult once the read
     * completes.
     */
    public CompletableFuture<ReadResult> read(long address) {
        return router.sendMessageAndGetCompletable(new LogUnitReadRequestMsg(address));
    }

    /**
     * Send a hint to the logging unit that a stream can be trimmed.
     *
     * @param stream The stream to trim.
     * @param prefix The prefix of the stream, as a global physical offset, to trim.
     */
    public void trim(UUID stream, long prefix) {

        router.sendMessage(new LogUnitTrimMsg(prefix, stream));
    }

    /**
     * Fill a hole at a given address.
     *
     * @param address The address to fill a hole at.
     */
    public CompletableFuture<Boolean> fillHole(long address) {
        return router.sendMessageAndGetCompletable(new LogUnitFillHoleMsg(address));
    }

    /**
     * Force the garbage collector to begin garbage collection.
     */
    public void forceGC() {
        router.sendMessage(new CorfuMsg(CorfuMsg.NettyCorfuMsgType.FORCE_GC));
    }

    /**
     * Change the default garbage collection interval.
     *
     * @param millis    The new garbage collection interval, in milliseconds.
     */
    public void setGCInterval(long millis) {
        router.sendMessage(new LogUnitGCIntervalMsg(millis));
    }

}

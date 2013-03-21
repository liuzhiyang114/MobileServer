package com.smartlab.net.grizzly;

import java.io.IOException;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.memory.MemoryManager;


public class SMARTFilter extends BaseFilter{
	
	private static final int HEADER_SIZE = 21;

    /**
     * Method is called, when new data was read from the Connection and ready
     * to be processed.
     *
     * We override this method to perform Buffer -> GIOPMessage transformation.
     * 
     * @param ctx Context of {@link FilterChainContext} processing
     * @return the next action
     * @throws java.io.IOException
     */
    @Override
    public NextAction handleRead(final FilterChainContext ctx) throws IOException {
        // Get the source buffer from the context
        final Buffer sourceBuffer = ctx.getMessage();

        final int sourceBufferLength = sourceBuffer.remaining();

        // If source buffer doesn't contain header
        if (sourceBufferLength < HEADER_SIZE) {
            // stop the filterchain processing and store sourceBuffer to be
            // used next time
            return ctx.getStopAction(sourceBuffer);
        }

        // Get the body length
        final int bodyLength = sourceBuffer.getInt(HEADER_SIZE - 12);
        // The complete message length
        final int completeMessageLength = HEADER_SIZE + bodyLength;
        
        // If the source message doesn't contain entire body
        if (sourceBufferLength < completeMessageLength) {
            // stop the filterchain processing and store sourceBuffer to be
            // used next time
            return ctx.getStopAction(sourceBuffer);
        }

        // Check if the source buffer has more than 1 complete GIOP message
        // If yes - split up the first message and the remainder
        final Buffer remainder = sourceBufferLength > completeMessageLength ? 
            sourceBuffer.split(completeMessageLength) : null;

        // Construct a SMART message
        final SMARTMessage smartMessage = new SMARTMessage();

        // Set GIOP header bytes
        smartMessage.setSMARTHeader(sourceBuffer.get(), sourceBuffer.get(),
                sourceBuffer.get(), sourceBuffer.get(),sourceBuffer.get());

        // Set major version
        smartMessage.setMajor(sourceBuffer.get());

        // Set minor version
        smartMessage.setMinor(sourceBuffer.get());

        // Set flags
        smartMessage.setFlags(sourceBuffer.get());

        // Set value
        smartMessage.setValue(sourceBuffer.get());

        // Set body length
        smartMessage.setBodyLength(sourceBuffer.getInt());

        // Set taskId
        smartMessage.setTaskId(sourceBuffer.getLong());
        
        // Read body
        final byte[] body = new byte[bodyLength];
        sourceBuffer.get(body);
        // Set body
        smartMessage.setBody(body);

        ctx.setMessage(smartMessage);

        // We can try to dispose the buffer
        sourceBuffer.tryDispose();

        // Instruct FilterChain to store the remainder (if any) and continue execution
        return ctx.getInvokeAction(remainder);
    }

    /**
     * Method is called, when we write a data to the Connection.
     *
     * We override this method to perform GIOPMessage -> Buffer transformation.
     *
     * @param ctx Context of {@link FilterChainContext} processing
     * @return the next action
     * @throws java.io.IOException
     */
    @Override
    public NextAction handleWrite(final FilterChainContext ctx) throws IOException {
        // Get the source SMART message to be written
        final SMARTMessage smartMessage = ctx.getMessage();

        final int size = HEADER_SIZE + smartMessage.getBodyLength();

        // Retrieve the memory manager
        final MemoryManager memoryManager =
                ctx.getConnection().getTransport().getMemoryManager();

        // allocate the buffer of required size
        final Buffer output = memoryManager.allocate(size);

        // Allow Grizzly core to dispose the buffer, once it's written
        output.allowBufferDispose(true);
        
        // SMART header
        output.put(smartMessage.getSMARTHeader());

        // Major version
        output.put(smartMessage.getMajor());

        // Minor version
        output.put(smartMessage.getMinor());

        // Flags
        output.put(smartMessage.getFlags());

        // Value
        output.put(smartMessage.getValue());

        // Body length
        output.putInt(smartMessage.getBodyLength());

        // taskId
        output.putLong(smartMessage.getTaskId());
        
        // Body
        output.put(smartMessage.getBody());

        // Set the Buffer as a context message
        ctx.setMessage(output.flip());

        // Instruct the FilterChain to call the next filter
        return ctx.getInvokeAction();
    }

}

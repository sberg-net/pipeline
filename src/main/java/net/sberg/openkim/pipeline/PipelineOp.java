package net.sberg.openkim.pipeline;

import java.util.Map;
import java.util.Objects;

/**
 * <p>PipelineOp interface.</p>
 *
 * @author stephan
 */
@FunctionalInterface
public interface PipelineOp {
    /**
     * <p>execute.</p>
     *
     * @param input a {@link Map} object
     * @return a {@link Map} object
     * @throws jakarta.mail.MessagingException if any.
     * @throws java.io.FileNotFoundException if any.
     * @throws AtomicInputException if any.
     */
    Map<String,Object> execute(Map input) throws Exception;
    /**
     * <p>andThen.</p>
     *
     * @param after a {@link PipelineOp} object
     * @return a {@link PipelineOp} object
     */
    default PipelineOp andThen(PipelineOp after) {
        Objects.requireNonNull(after);
        return (Map t) -> after.execute(execute(t));
    }
}

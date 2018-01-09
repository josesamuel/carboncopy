package carboncopy.compiler;

import com.google.auto.service.AutoService;

import javax.annotation.processing.Processor;

/**
 * Java processor entry point
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends CarbonCopyProcessor {
}

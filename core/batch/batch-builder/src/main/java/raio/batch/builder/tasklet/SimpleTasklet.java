package raio.batch.builder.tasklet;

import org.springframework.batch.repeat.RepeatStatus;

@FunctionalInterface
public interface SimpleTasklet {
    
    RepeatStatus execute() throws Exception;
}
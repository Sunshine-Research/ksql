package io.confluent.ksql.util;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.processing.log.ProcessingLogMessageSchema;
import io.confluent.ksql.processing.log.ProcessingLogMessageSchema.MessageType;
import java.io.IOException;
import java.util.List;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.junit.Test;

public class EngineProcessingLogMessageFactoryTest {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final String errorMsg = "error msg";

  @Test
  @SuppressWarnings("unchecked")
  public void shouldBuildRecordProcessingErrorCorrectly() throws IOException {
    // When:
    final SchemaAndValue msgAndSchema = EngineProcessingLogMessageFactory.recordProcessingError(
        errorMsg, new GenericRow(123, "data")
    ).get();

    // Then:
    assertThat(msgAndSchema.schema(), equalTo(ProcessingLogMessageSchema.PROCESSING_LOG_SCHEMA));
    final Struct msg = (Struct) msgAndSchema.value();
    assertThat(
        msg.get(ProcessingLogMessageSchema.TYPE),
        equalTo(MessageType.RECORD_PROCESSING_ERROR.getTypeId()));
    assertThat(msg.get(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR), notNullValue());
    final Struct recordProcessingError =
        msg.getStruct(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR);
    assertThat(
        recordProcessingError.get(
            ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR_FIELD_MESSAGE),
        equalTo(errorMsg));
    final List<Object> rowAsList =
        OBJECT_MAPPER.readValue(
            recordProcessingError.getString(
                ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR_FIELD_RECORD),
            List.class
        );
    assertThat(rowAsList, contains(123, "data"));
  }

  @Test
  public void shouldBuildRecordProcessingErrorCorrectlyIfRowNull() {
    // When:
    final SchemaAndValue msgAndSchema = EngineProcessingLogMessageFactory.recordProcessingError(
        errorMsg, null
    ).get();

    // Then:
    final Struct msg = (Struct) msgAndSchema.value();
    final Struct recordProcessingError =
        msg.getStruct(ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR);
    assertThat(
        recordProcessingError.get(
            ProcessingLogMessageSchema.RECORD_PROCESSING_ERROR_FIELD_RECORD),
        nullValue());
  }
}
package tictactoe.datasource.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;

@Converter
public class IntArrayToJsonConverter implements AttributeConverter<int[][], String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(int[][] ints) {
        try {
            return mapper.writeValueAsString(ints);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert int[][] to JSON", e);
        }
    }

    @Override
    public int[][] convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, int[][].class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to int[][]", e);
        }
    }
}

package com.example.sharedjavagemini;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

public class SqlWhereBuilder {

    public static String buildWhereClauseFromJson(String jsonString) {
        try {
            // Step 1: Parse JSON into Java class
            ObjectMapper objectMapper = new ObjectMapper();
            FilterCriteriaWrapper wrapper = objectMapper.readValue(jsonString, FilterCriteriaWrapper.class);

            // Step 2: Validate and process the filter criteria
            List<FilterCriteria> filterCriteriaList = wrapper.getFilterCriteria();
            if (filterCriteriaList == null || filterCriteriaList.isEmpty()) {
                return ""; // No filters provided
            }

            // Step 3: Build the WHERE clause dynamically
            String whereClause = filterCriteriaList.stream()
                    .map(SqlWhereBuilder::buildCondition)
                    .collect(Collectors.joining(" ")); // Join with logical links (e.g., AND, OR)

            return "WHERE " + whereClause;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse and process JSON", e);
        }
    }

    // Helper function to translate a single filter criterion to SQL
    private static String buildCondition(FilterCriteria criteria) {
        // Validate required fields
        if (criteria.getColumn() == null || criteria.getOperator() == null || criteria.getValue() == null) {
            throw new IllegalArgumentException("Invalid filter criteria: " + criteria);
        }

        // Map operator to SQL equivalent
        String operator = mapOperator(criteria.getOperator());

        // Build condition
        String condition = String.format("%s %s '%s'", criteria.getColumn(), operator, criteria.getValue());

        // Add logical link (e.g., AND, OR)
        String link = criteria.getLink() != null ? criteria.getLink().toUpperCase() : "AND";
        return condition + " " + link;
    }

    // Map custom operators like "GE", "LE", etc., to SQL operators
    private static String mapOperator(String operator) {
        switch (operator.toUpperCase()) {
            case "GE": return ">=";
            case "LE": return "<=";
            case "EQ": return "=";
            case "NE": return "<>";
            case "GT": return ">";
            case "LT": return "<";
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    public static void main(String[] args) {
        // Example JSON input
        String jsonString = "{\n" +
                "  \"filterCriteria\": [\n" +
                "    {\"filterType\": \"field\", \"column\": \"tata\", \"operator\": \"GE\", \"value\": \"6MONTHS\", \"link\": \"and\"},\n" +
                "    {\"filterType\": \"field\", \"column\": \"status\", \"operator\": \"EQ\", \"value\": \"ACTIVE\", \"link\": \"or\"}\n" +
                "  ]\n" +
                "}";

        // Build WHERE clause
        String whereClause = buildWhereClauseFromJson(jsonString);

        // Print output
        System.out.println(whereClause);
    }
}

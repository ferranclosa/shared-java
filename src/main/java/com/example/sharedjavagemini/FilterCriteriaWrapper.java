package com.example.sharedjavagemini;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// Root JSON object
public class FilterCriteriaWrapper {
    @JsonProperty("filterCriteria")
    private List<FilterCriteria> filterCriteria;

    public List<FilterCriteria> getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(List<FilterCriteria> filterCriteria) {
        this.filterCriteria = filterCriteria;
    }
}

// Single filter criterion
class FilterCriteria {
    private String filterType;
    private String column;
    private String operator;
    private String value;
    private String link;

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "FilterCriteria{" +
                "filterType='" + filterType + '\'' +
                ", column='" + column + '\'' +
                ", operator='" + operator + '\'' +
                ", value='" + value + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
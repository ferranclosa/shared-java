import React from "react";
import "./JsonViewer.css"; // CSS file for basic styling

const JsonViewer = ({ data, indentLevel = 0 }) => {
    const isObject = (value) => typeof value === "object" && value !== null;

    if (!isObject(data)) {
        return <div className="json-value">{`${data}`}</div>; // Show primitive types
    }

    const getMargin = (level) => {
        return { marginLeft: `${level * 20}px` }; // Indentation based on level
    };

    return (
        <div className="json-viewer">
            {Object.keys(data).map((key) => (
                <div key={key} style={getMargin(indentLevel)}>
                    <strong>{key}:</strong>{" "}
                    {isObject(data[key]) ? (
                        <JsonViewer data={data[key]} indentLevel={indentLevel + 1} />
                    ) : (
                        <span className="json-value">{`${data[key]}`}</span>
                    )}
                </div>
            ))}
        </div>
    );
};

export default JsonViewer;
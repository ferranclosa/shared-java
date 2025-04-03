/**
 * Map of compatible types. Each group represents compatible data types.
 */
const compatibilityGroups = {
    string: ["string", "text", "varchar", "char", "nvarchar", "nchar", "clob"],
    number: ["number", "integer", "int", "float", "double", "decimal", "bigint", "smallint"],
    date: ["date", "datetime", "timestamp", "time"],
    boolean: ["boolean", "bool", "bit"]
};

/**
 * Normalize a data type to a standard format.
 * @param {string} dataType - The data type to normalize.
 * @returns {string} - The normalized data type (e.g., 'string', 'number', 'date', etc.).
 */
function normalizeDataType(dataType) {
    if (!dataType || typeof dataType !== "string") return null;

    // Convert to lowercase and trim unnecessary whitespace
    const normalized = dataType.toLowerCase().trim();

    // Find the group that this data type belongs to
    for (const [group, types] of Object.entries(compatibilityGroups)) {
        if (types.includes(normalized)) {
            return group; // Return the standardized group (e.g., 'string', 'number', etc.)
        }
    }

    // If no match was found, return null (unknown type)
    return null;
}

/**
 * Check if two data types are compatible.
 * @param {string} dataType1 - The first data type.
 * @param {string} dataType2 - The second data type.
 * @returns {boolean} - True if the data types are compatible, false otherwise.
 */
function areDataTypesCompatible(dataType1, dataType2) {
    const normalizedType1 = normalizeDataType(dataType1);
    const normalizedType2 = normalizeDataType(dataType2);

    // If either type is null (unknown), they are not compatible
    if (!normalizedType1 || !normalizedType2) {
        return false;
    }

    // Check if the normalized types belong to the same compatibility group
    return normalizedType1 === normalizedType2;
}

/**
 * List all compatible types for a given type (optional utility function).
 * @param {string} dataType - The data type to check compatibility for.
 * @returns {string[]} - Array of compatible data types.
 */
function getCompatibleDataTypes(dataType) {
    const normalizedType = normalizeDataType(dataType);

    if (!normalizedType) {
        return [];
    }

    // Return all compatible types within the same group
    return compatibilityGroups[normalizedType];
}
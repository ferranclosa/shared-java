import React, { useState } from "react";
import { useTable } from "react-table";
import Select from "react-select";
import moment from "moment";

// Initial data
const initialData = [
    { id: 1, name: "John Doe", role: "Admin", startDate: "2023-10-01" },
    { id: 2, name: "Jane Smith", role: "User", startDate: "2023-09-15" },
];

// Options for the "select" input
const roleOptions = [
    { value: "Admin", label: "Admin" },
    { value: "User", label: "User" },
    { value: "Guest", label: "Guest" },
];

// Editable Cell Component with Validation
const EditableCell = ({
                          value: initialValue,
                          row: { index },
                          column: { id, meta },
                          updateTableData,
                      }) => {
    const [value, setValue] = useState(initialValue);
    const [error, setError] = useState("");

    // Synchronize state with parent for initial value
    React.useEffect(() => {
        setValue(initialValue);
    }, [initialValue]);

    const validate = (newValue) => {
        let validationError = ""; // Default is no error

        if (meta) {
            if (meta.required && !newValue) {
                validationError = "This field is required.";
            } else if (meta.minLength && newValue.length < meta.minLength) {
                validationError = `Minimum length is ${meta.minLength} characters.`;
            } else if (meta.maxLength && newValue.length > meta.maxLength) {
                validationError = `Maximum length is ${meta.maxLength} characters.`;
            } else if (meta.type === "date" && meta.futureOnly) {
                const today = moment().startOf("day");
                const selectedDate = moment(newValue);
                if (!selectedDate.isAfter(today)) {
                    validationError = "The date must be in the future.";
                }
            }
        }

        setError(validationError);
        return validationError === ""; // Return true if no error
    };

    const handleChange = (value) => {
        const isValid = validate(value);
        setValue(value);
        if (isValid) {
            updateTableData(index, id, value);
        }
    };

    // Input types based on the meta property
    if (meta) {
        switch (meta.type) {
            case "text":
                return (
                    <div style={{ position: "relative" }}>
                        <input
                            type="text"
                            value={value}
                            onChange={(e) => handleChange(e.target.value)}
                            onBlur={(e) => validate(e.target.value)}
                            style={{
                                width: "100%",
                                border: error ? "1px solid red" : "1px solid #ccc",
                                padding: "5px",
                                borderRadius: "4px",
                            }}
                        />
                        {error && (
                            <div style={{ color: "red", fontSize: "12px", marginTop: "5px" }}>
                                {error}
                            </div>
                        )}
                    </div>
                );

            case "select":
                return (
                    <div style={{ position: "relative" }}>
                        <Select
                            value={roleOptions.find((option) => option.value === value)}
                            onChange={(selectedOption) =>
                                handleChange(selectedOption?.value || "")
                            }
                            options={roleOptions}
                        />
                        {error && (
                            <div style={{ color: "red", fontSize: "12px", marginTop: "5px" }}>
                                {error}
                            </div>
                        )}
                    </div>
                );

            case "date":
                return (
                    <div style={{ position: "relative" }}>
                        <input
                            type="date"
                            value={value}
                            onChange={(e) => handleChange(e.target.value)}
                            onBlur={(e) => validate(e.target.value)}
                            style={{
                                width: "100%",
                                border: error ? "1px solid red" : "1px solid #ccc",
                                padding: "5px",
                                borderRadius: "4px",
                            }}
                        />
                        {error && (
                            <div style={{ color: "red", fontSize: "12px", marginTop: "5px" }}>
                                {error}
                            </div>
                        )}
                    </div>
                );

            default:
                return <span>{value}</span>;
        }
    }

    return <span>{value}</span>;
};

const DynamicEditableTable = () => {
    const [data, setData] = useState(initialData);
    const [newRow, setNewRow] = useState({
        name: "",
        role: "Admin",
        startDate: "",
    });

    const columns = React.useMemo(
        () => [
            {
                Header: "Name",
                accessor: "name",
                meta: { type: "text", required: true, minLength: 3, maxLength: 20 },
                Cell: EditableCell,
            },
            {
                Header: "Role",
                accessor: "role",
                meta: { type: "select", required: true },
                Cell: EditableCell,
            },
            {
                Header: "Start Date",
                accessor: "startDate",
                meta: { type: "date", required: true, futureOnly: true },
                Cell: EditableCell,
            },
            {
                Header: "Actions",
                accessor: "actions",
                Cell: ({ row: { index } }) => (
                    <button
                        onClick={() => handleDelete(index)}
                        style={{
                            backgroundColor: "red",
                            color: "white",
                            cursor: "pointer",
                            padding: "5px 10px",
                            border: "none",
                        }}
                    >
                        Delete
                    </button>
                ),
            },
        ],
        []
    );

    const updateTableData = (rowIndex, columnId, value) => {
        setData((oldData) =>
            oldData.map((row, index) => {
                if (index === rowIndex) {
                    return {
                        ...row,
                        [columnId]: value,
                    };
                }
                return row;
            })
        );
    };

    const handleAddRow = () => {
        setData((prevData) => [
            ...prevData,
            { id: prevData.length + 1, ...newRow },
        ]);
        setNewRow({ name: "", role: "Admin", startDate: "" }); // Reset form
    };

    const handleDelete = (rowIndex) => {
        setData((prevData) => prevData.filter((_, index) => index !== rowIndex));
    };

    // React-Table hook
    const tableInstance = useTable({
        columns,
        data,
        defaultColumn: { Cell: EditableCell },
        updateTableData,
    });

    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
    } = tableInstance;

    return (
        <div style={{ padding: "20px" }}>
            <h1>Dynamic Editable Table with Validation</h1>

            <div style={{ marginBottom: "20px", display: "flex", gap: "10px" }}>
                <input
                    type="text"
                    placeholder="Enter Name"
                    value={newRow.name}
                    onChange={(e) => setNewRow({ ...newRow, name: e.target.value })}
                    style={{ padding: "8px", border: "1px solid #ccc", borderRadius: "4px" }}
                />
                <Select
                    value={roleOptions.find((option) => option.value === newRow.role)}
                    onChange={(selectedOption) =>
                        setNewRow({ ...newRow, role: selectedOption.value })
                    }
                    options={roleOptions}
                    placeholder="Select Role"
                    styles={{
                        container: (base) => ({
                            ...base,
                            width: "200px",
                        }),
                    }}
                />
                <input
                    type="date"
                    value={newRow.startDate}
                    onChange={(e) => setNewRow({ ...newRow, startDate: e.target.value })}
                    style={{ padding: "8px", border: "1px solid #ccc", borderRadius: "4px" }}
                />
                <button
                    onClick={handleAddRow}
                    style={{
                        backgroundColor: "green",
                        color: "white",
                        border: "none",
                        padding: "8px 16px",
                        cursor: "pointer",
                    }}
                >
                    Add Row
                </button>
            </div>

            <table
                {...getTableProps()}
                style={{
                    border: "1px solid black",
                    width: "100%",
                    borderCollapse: "collapse",
                }}
            >
                <thead>
                {headerGroups.map((headerGroup) => (
                    <tr {...headerGroup.getHeaderGroupProps()}>
                        {headerGroup.headers.map((column) => (
                            <th
                                {...column.getHeaderProps()}
                                style={{
                                    border: "1px solid black",
                                    backgroundColor: "#f1f1f1",
                                    padding: "10px",
                                }}
                            >
                                {column.render("Header")}
                            </th>
                        ))}
                    </tr>
                ))}
                </thead>
                <tbody {...getTableBodyProps()}>
                {rows.map((row) => {xoju
                    prepareRow(row);
                    return (
                        <tr {...row.getRowProps()}>
                            {row.cells.map((cell) => (
                                <td
                                    {...cell.getCellProps()}
                                    style={{
                                        border: "1px solid black",
                                        padding: "10px",
                                        textAlign: "center",
                                    }}
                                >
                                    {cell.render("Cell", { updateTableData })}
                                </td>
                            ))}
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    );
};

export default DynamicEditableTable;
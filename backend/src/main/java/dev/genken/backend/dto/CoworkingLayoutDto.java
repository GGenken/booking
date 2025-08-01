package dev.genken.backend.dto;

public class CoworkingLayoutDto {
    private int rows;
    private int cols;

    public CoworkingLayoutDto(int rows, int cols) {
        setRows(rows);
        setCols(cols);
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }
}

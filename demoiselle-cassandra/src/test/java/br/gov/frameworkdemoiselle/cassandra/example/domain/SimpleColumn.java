package br.gov.frameworkdemoiselle.cassandra.example.domain;

import br.gov.frameworkdemoiselle.cassandra.annotation.Column;
import br.gov.frameworkdemoiselle.cassandra.annotation.ColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Key;
import br.gov.frameworkdemoiselle.cassandra.annotation.SecondaryColumnFamily;
import br.gov.frameworkdemoiselle.cassandra.annotation.Value;

@ColumnFamily("Standard1")
@SecondaryColumnFamily("Standard2")
public class SimpleColumn {

	@Key
	private Long id;
	
	@Column
	private String column;
	
	@Value
	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Column [id=" + id + ", column=" + column + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleColumn other = (SimpleColumn) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}

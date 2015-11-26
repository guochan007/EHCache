import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class TEST implements Serializable {
	private static final long serialVersionUID = 1L;
	public Long TEST_ID;
	public String TEST_NAME;
	public Timestamp TEST_TIME;
	public BigDecimal TEST_VALUE;
	
	@Override
	public String toString() {
		return String.format("ID:%s,NAME:%s", TEST_ID, TEST_NAME);
	}
}
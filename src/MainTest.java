import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class MainTest {

	/**
	 * 测试ehcache
	 */
	public static void main(String[] args) {
		try {
//			1 插入
//			insert(20000);
			
//			2 从map中查
//			mapCache();
			
//			3 从ehcache中查
			ehcache();
			
//			将数据放入map时间 Time:140
//			ID:19888,NAME:SFPpqIjmQLZ6G419zYTqKA0p8Ax88CwFxY
//			从map读取数据时间 Time:0
			
//			将数据放入catch时间 Time:234
//			ID:19888,NAME:SFPpqIjmQLZ6G419zYTqKA0p8Ax88CwFxY
//			从cache读取数据时间 Time:0
			
//			这结果说明了个屁，用cache快在哪了，难道就是跟直接从数据库取相比吗？
			
//			4 不用缓存  直接查表
			getFromDB(19888L);
//			19888,SFPpqIjmQLZ6G419zYTqKA0p8Ax88CwFxY
//			直接从数据库查询数据耗时 Time:8    15    0   
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	// 测试 插入数据：
	public static void insert(int total) throws Exception {
		Thread.sleep(3000);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Timestamp current = new Timestamp(System.currentTimeMillis());
		String currentStr = sdf.format(current);
		System.out.println(currentStr);
		Connection conn = getCon();
		try {
			long begin = System.currentTimeMillis();
			conn.setAutoCommit(false);
			String sql = "INSERT INTO TEST (TEST_ID,TEST_NAME,TEST_TIME,TEST_VALUE) VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 1; i <= total; i++) {
				ps.setLong(1, i);
				ps.setString(2, Util.genString(33));
				ps.setTimestamp(3, current);
				ps.setBigDecimal(4, new BigDecimal(Util.genDouble()));
				ps.addBatch();
				if ((i % 500) == 0) {
					ps.executeBatch();
				}
			}
			ps.executeBatch();
			conn.commit();
			long end = System.currentTimeMillis();
			System.out.printf("Count:%d Time:%d\n", total, (end - begin));
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
		} finally {
			conn.close();
		}
	}

	//1 缓存到map中：
	public static void mapCache() throws Exception {
		HashMap<Long, TEST> map = new HashMap<Long, TEST>();
		Connection conn = getCon();
		try {
			long begin = System.currentTimeMillis();
			Statement s = conn.createStatement();
			String sql = "SELECT TEST_ID,TEST_NAME,TEST_TIME,TEST_VALUE FROM TEST";
			ResultSet querySet = s.executeQuery(sql);
			for (int i = 1; querySet.next(); i++) {
				TEST curr = new TEST();
				curr.TEST_ID = querySet.getLong(1);
				curr.TEST_NAME = querySet.getString(2);
				curr.TEST_TIME = querySet.getTimestamp(3);
				curr.TEST_VALUE = querySet.getBigDecimal(4);
				map.put(curr.TEST_ID, curr);
			}
			long end = System.currentTimeMillis();
			System.out.printf("将数据放入map时间 Time:%d\n", (end - begin));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		
		long begin1 = System.currentTimeMillis();
		System.out.println(map.get(19888L));
		long end1 = System.currentTimeMillis();
		System.out.printf("从map读取数据时间 Time:%d\n", (end1 - begin1));
	}

	// 2 缓存到cache中
	public static void ehcache() throws Exception {
		CacheManager manager = CacheManager.create("src/ehcache.xml");
		manager.addCache("TEST_ID.TEST");
		Cache cache = manager.getCache("TEST_ID.TEST");
		Connection conn =getCon();
		try {
			long begin = System.currentTimeMillis();
			Statement s = conn.createStatement();
			String sql = "SELECT TEST_ID,TEST_NAME,TEST_TIME,TEST_VALUE FROM TEST";
			ResultSet querySet = s.executeQuery(sql);
			for (int i = 1; querySet.next(); i++) {
				TEST curr = new TEST();
				curr.TEST_ID = querySet.getLong(1);
				curr.TEST_NAME = querySet.getString(2);
				curr.TEST_TIME = querySet.getTimestamp(3);
				curr.TEST_VALUE = querySet.getBigDecimal(4);
				cache.put(new Element(curr.TEST_ID, curr));
			}
			long end = System.currentTimeMillis();
			System.out.printf("将数据放入catch时间 Time:%d\n", (end - begin));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		
		long begin1 = System.currentTimeMillis();
		Element element = cache.get(new Long(19888));
		System.out.println(element.getValue());
		long end1 = System.currentTimeMillis();
		System.out.printf("从cache读取数据时间 Time:%d\n", (end1 - begin1));
	}

	
// 3 不用缓存 直接从表里查
	private static void getFromDB(Long id) throws Exception{
		Connection conn =getCon();
		try {
			Statement sm = conn.createStatement();
			String sql = "SELECT TEST_ID,TEST_NAME,TEST_TIME,TEST_VALUE FROM TEST where TEST_ID="+id;
			long begin = System.currentTimeMillis();
			ResultSet rs = sm.executeQuery(sql);
			rs.next();
			System.out.println(rs.getString("TEST_ID")+","+rs.getString("TEST_NAME"));
			long end = System.currentTimeMillis();
			System.out.printf("直接从数据库查询数据耗时 Time:%d\n", (end - begin));
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
	}
	
//	获得连接
	private static Connection getCon() throws Exception{
		Class.forName("com.mysql.jdbc.Driver");
		String url="jdbc:mysql://localhost:3306/test";
		String username="root";
		String password="000000";
		Connection con=null;
		try {
			con = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
}

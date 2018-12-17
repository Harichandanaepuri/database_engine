
public class Payload {
	public static short calPayloadSize(String[] v, String[] dType)
	{
		int value = 1 + dType.length - 1; 
		for(int i = 1; i < dType.length; i++){
			String dt = dType[i];
			switch(dt){
				case "TINYINT":
					value = value + DataTypes.TINYINT;
					break;
				case "SMALLINT":
					value = value + DataTypes.SMALLINT;
					break;
				case "INT":
					value = value + DataTypes.INT;
					break;
				case "BIGINT":
					value = value + DataTypes.BIGINT;
					break;
				case "REAL":
					value = value + DataTypes.REAL;
					break;		
				case "DOUBLE":
					value = value + DataTypes.DOUBLE;
					break;
				case "DATETIME":
					value = value + DataTypes.DATETIME;
					break;
				case "DATE":
					value = value + DataTypes.DATE;
					break;
				case "TEXT":
					String text = v[i];
					int len = text.length();
					value = value + len;
					break;
				default:
					break;
			}
		}
		return (short)value;
	}


}

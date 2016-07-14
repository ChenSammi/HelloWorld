
import java.security.SecureRandom;

public class PasswordGenerator {
	private static int pwdLen = 10;
	private static SecureRandom rand = new SecureRandom();

	static final private int FULL_LOW = 33;
	static final private int FULL_HIGH = 126;
	static final private int UPPER_LOW = 65;
	static final private int UPPER_HIGH = 90;
	static final private int LOWER_LOW = 97;
	static final private int LOWER_HIGH = 122;
	static final private int NUM_LOW = 48;
	static final private int NUM_HIGH = 57;
	static final private char[] NON_ALPHANUM =
	{ 33, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
	        58, 59, 60, 61, 62, 63, 64, 91, 93, 94, 95, 96, 123, 124, 125, 126 };
	
	static final String POSTGRESQL_JDBC_DRV = "postgresql-9.3-1102.jdbc4.jar";

	private static char genRndCharInRange(int start, int end) {
		int code = rand.nextInt(end - start + 1);
		code = code + start;
		return (char) code;
	}

	private static char genRndCharInNonAlphaNum() {
		int index = rand.nextInt(NON_ALPHANUM.length);
		return NON_ALPHANUM[index];
	}

	// make sure empty location is larger than invoke time
	// or it will be endless loop
	private static int genRndLocation(char[] pwd) {
		boolean find = false;
		int loc = -1;
		do {
			loc = rand.nextInt(pwdLen);
			if (pwd[loc] == 0)
				find = true;
		} while (!find);

		return loc;
	}

	private static String genRndPwd() {

		char[] pwd = new char[pwdLen];
		for (int i = 0; i < pwdLen; i++)
			pwd[i] = 0;

		// generate uppercase char
		char upChar = genRndCharInRange(UPPER_LOW, UPPER_HIGH);
		int loc = genRndLocation(pwd);
		pwd[loc] = upChar;

		// generate lowercase char
		char lowChar = genRndCharInRange(LOWER_LOW, LOWER_HIGH);
		loc = genRndLocation(pwd);
		pwd[loc] = lowChar;

		// generate numeric char
		char numChar = genRndCharInRange(NUM_LOW, NUM_HIGH);
		loc = genRndLocation(pwd);
		pwd[loc] = numChar;

		// generate non-alphanum char
		char nonChar = genRndCharInNonAlphaNum();
		loc = genRndLocation(pwd);
		pwd[loc] = nonChar;

		// paddle unpadded slots
		for (int i = 0; i < pwdLen; i++) {
			if (pwd[i] == 0) {
				char c = genRndCharInRange(FULL_LOW, FULL_HIGH);

				// prevent " and \
				while (c == 34 || c == 92)
				{
					c = genRndCharInRange(FULL_LOW, FULL_HIGH);
				}

				pwd[i] = c;
			}
		}

		return String.valueOf(pwd);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// generate random password based on rule
		// 1. password length between [8,64]
		// 2. characters include each of following categories:
		// uppercase, lowercase, numeric and non-alphanumeric
		// (printable ascii char in [32, 126], excluding first 3 categories)

		if (args.length == 1) {
			// use user defined length
			try {
				pwdLen = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("pwd_len (" + args[0] + ") is not a number");
				System.out.println("failed");
				return;
			}

			if (pwdLen < 8 || pwdLen > 64) {
				System.out.println("pwd_len (" + args[0]
				        + ") does not meet the length criteria [8,64]");
				System.out.println("failed");
				return;
			}
		} else if (args.length > 1) {
			System.out.println("usage: java -cp " +
			        "Utility.jar;" + POSTGRESQL_JDBC_DRV + " " +
			        "com.intel.parkridge.install.PasswordGenerator pwd_len[optional]");
			System.out.println("failed");
			return;
		}

		System.out.println(genRndPwd());
	}
}

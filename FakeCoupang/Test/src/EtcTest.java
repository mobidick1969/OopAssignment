import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Coupang on 2015. 12. 26..
 */
public class EtcTest {

	public static void main(String... args) throws IOException, InterruptedException, ParseException {

		Option orderOption = Option.builder("order")
									.hasArgs()
									.argName("product1> <product2> ... <productN> <coupang_id> <user_name")
									.build();

		Option lookupOption = Option.builder("lookup")
									.hasArgs()
									.argName("order_id=value> <coupang_id=value> <user_name=value")
									.numberOfArgs(2)
									.valueSeparator()
									.build();

		Option logisticsOption = Option.builder("logistics")
										.hasArgs()
										.argName("order_id> <none|ing|end")
										.numberOfArgs(2)
										.build();

		Option quitOption = Option.builder("quit")
								.build();


		Options options = new Options();
		options.addOption(orderOption);
		options.addOption(lookupOption);
		options.addOption(logisticsOption);
		options.addOption(quitOption);

		CommandLineParser parser = new DefaultParser();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		HelpFormatter helpFormatter = new HelpFormatter();
		String[] optionValues = null;

		while(true) {
			System.out.print("coupang >> ");
			String line = reader.readLine().trim();
			if( !line.isEmpty() ) {
				String gotArgs[] = line.split("\\s+");
				for(int i = 0 ; i<gotArgs.length ; ++i ) {
					gotArgs[i] = gotArgs[i].trim().toLowerCase();
				}

				try {
					CommandLine cmdLine = parser.parse(options, gotArgs);

					if( cmdLine.hasOption("quit") ) {
						break;
					} else if( cmdLine.hasOption("order") ) {

						optionValues = cmdLine.getOptionValues("order");
						if( optionValues.length < 3 ) {
							throw new IllegalArgumentException();
						}

						//오더 만듬

					} else if( cmdLine.hasOption("lookup") ) {

						Properties properties = cmdLine.getOptionProperties("lookup");

						if( properties.getProperty("order_id") != null ) {

						} else if(properties.getProperty("coupang_id") != null ) {

						} else if(properties.getProperty("user_name") != null ) {

						} else {
							throw new IllegalArgumentException();
						}

					} else if(cmdLine.hasOption("logistics") ) {

						optionValues = cmdLine.getOptionValues("logistics");
						if( optionValues.length != 2 ) {
							throw new  IllegalArgumentException();
						}

						optionValues[1] = optionValues[1].toLowerCase();

						if( !optionValues[1].equals("end") &&
							!optionValues[1].equals("ing") &&
							!optionValues[1].equals("none") ) {
							throw new  IllegalArgumentException();
						}

					}
				} catch(Exception e) {
					helpFormatter.printHelp("coupang", options);
				}

			}
		}

		reader.close();
		System.out.println("coupang >> Bye!");

	}

}

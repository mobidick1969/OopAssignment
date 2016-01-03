package fakecoupangsystem.launch;

import fakecoupangsystem.domain.Order;
import fakecoupangsystem.domain.Product;
import fakecoupangsystem.domain.dto.OrderDto;
import fakecoupangsystem.exception.IllegalCoupangArgumentException;
import fakecoupangsystem.service.LogisticsService;
import fakecoupangsystem.service.LookupService;
import fakecoupangsystem.service.OrderService;
import fakecoupangsystem.service.ProductService;
import fakespring.annotation.Autowired;
import fakespring.annotation.Manager;
import fakespring.annotation.Meta;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

/**
 * Created by Coupang on 2015. 12. 26..
 */
@Manager(FakeCoupangConfig.class)
public class FakeCoupangSystem {
	@Autowired
	private LogisticsService logisticsService;
	@Autowired
	private LookupService lookupService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProductService productService;

	private FakeCoupangConfig config;
	private Options options;
	private CommandLineParser parser;
	private HelpFormatter helpFormatter;

	public FakeCoupangSystem() { }

	private void init() {
		Option orderOption = Option.builder("order")
			.hasArgs()
			.argName("product_id1> <product_id2> ... <product_idN> <coupang_id> <user_name")
			.build();

		Option lookupOption = Option.builder("lookup")
			.hasArgs()
			.argName("order_id=value> | <coupang_id=value")
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

		options = new Options();
		options.addOption(orderOption);
		options.addOption(lookupOption);
		options.addOption(logisticsOption);
		options.addOption(quitOption);

		helpFormatter = new HelpFormatter();
		parser = new DefaultParser();
	}

	private String[] readLine(BufferedReader reader) throws IOException {
		String line = reader.readLine().trim();
		String[] ret = null;
		if( !line.isEmpty() ) {

			ret = line.trim().split("\\s+");

			for(int i = 0 ; i<ret.length ; ++i ) {
				ret[i] = ret[i].trim().toLowerCase();
			}
		}

		return ret;
	}

	public void launch() throws IOException {
		init();

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		helpFormatter.printHelp("Coupang The World! Welcome!!", options);
		String prefix = "coupang >> ";

		if( config.isDevMode() ) {
			System.out.println("===========================");
			System.out.println("==== D E V M O D E ========");
			System.out.println(" WHEN YOU LEAVE DELETE ALL ");
			System.out.println("===========================");
			prefix = " [DEV] " + prefix;
		}

		while(true) {
			System.out.print(prefix);
			String gotArgs[] = readLine(reader);
			if( gotArgs != null ) {
				try {
					CommandLine cmdLine = parser.parse(options, gotArgs);

					if( cmdLine.hasOption("quit") ) {
						break;
					} else if( cmdLine.hasOption("order") ) {
						long orderId = order(cmdLine);
						System.out.println(prefix + " Received New Order! [ Order ID : " + orderId + "]");
					} else if( cmdLine.hasOption("lookup") ) {
						lookup(cmdLine);
					} else if(cmdLine.hasOption("logistics") ) {
						logistics(cmdLine);
					}
				} catch(Exception e) {
					e.printStackTrace();
					System.out.println("WRONG INPUT!!");
					helpFormatter.printHelp("coupang", options);
				}

			}
		}

		reader.close();
		System.out.println("coupang >> Bye!");
		quit();
	}

	private void quit() {
		if( config.isDevMode() ) {
			Stack<File> stack = new Stack<File>();
			File root = config.getRoot();
			stack.push(root);

			//remove directory
			while(!stack.isEmpty()) {
				File target = stack.pop();
				if( target.isDirectory() ) {
					File[] files = target.listFiles();
					if( files.length <= 0 ) {
						target.delete();
					} else {
						stack.push(target);
						for(File f : files ) {
							stack.push(f);
						}
					}
				} else {
					target.delete();
				}
			}
		}
	}

	private long order(CommandLine cmdLine) {

		String optionValues[] = cmdLine.getOptionValues("order");
		if( optionValues.length < 3 ) {
			throw new IllegalArgumentException();
		}

		//오더 만듬
		Order order = new Order();
		String userName = optionValues[optionValues.length-1];
		String coupangId = optionValues[optionValues.length-2];
		order.setDeliverStatus(Order.DeliverStatus.NONE);
		order.setOrdererName(userName);
		order.setCoupangId(coupangId);


		for(int i = 0 ; i<optionValues.length - 2 ; i++ ) {
			int productId = Integer.parseInt(optionValues[i].trim());

			if( productId < 0 || productId > 99 ) {
				throw new IllegalCoupangArgumentException("Invalid Product Id for [" +
						productId +
						"] Sorry. We have only 100 products!");
			}

			Product product = new Product();
			product.setId(productId);
			order.addProduct(product);
		}

		return orderService.orderProducts(order);
	}

	private void logistics(CommandLine cmdLine) {
		String optionValues[] = cmdLine.getOptionValues("logistics");
		if( optionValues.length != 2 ) {
			throw new  IllegalArgumentException();
		}

		optionValues[1] = optionValues[1].toLowerCase();

		if( !( optionValues[1].equals("end") ||
			optionValues[1].equals("ing") ||
			optionValues[1].equals("none") ) ) {
			throw new  IllegalArgumentException();
		}

		Order order = new Order();
		order.setOrderId(Long.parseLong(optionValues[0]));
		order.setDeliverStatus(Order.DeliverStatus.valueOf(optionValues[1].toUpperCase()));

		logisticsService.updateLogisticsStatus(order);
	}

	private void lookup(CommandLine cmdLine) {
		Properties properties = cmdLine.getOptionProperties("lookup");
		String value = null;
		List<OrderDto> orderDtos = new ArrayList<OrderDto>(1);

		if( ( value = properties.getProperty("order_id") ) != null ) {
			OrderDto orderDto = lookupService.lookupByOrderId(Long.parseLong(value));
			orderDtos.add(orderDto);

		} else if(( value = properties.getProperty("coupang_id") ) != null ) {
			orderDtos = lookupService.lookupByCoupangId(value);
		} else {
			throw new IllegalArgumentException();
		}

		printLookupedResults(orderDtos);
	}

	private void printLookupedResults(List<OrderDto> orderDtos) {
		System.out.println("Order Count : " + orderDtos.size());
		for(OrderDto orderDto : orderDtos ) {
			System.out.println(orderDto);
		}
	}

	@Meta
	public void setConfig(FakeCoupangConfig config) {
		this.config = config;
	}
}

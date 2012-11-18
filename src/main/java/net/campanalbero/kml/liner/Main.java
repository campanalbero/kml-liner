package net.campanalbero.kml.liner;

public class Main {
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("3 args need");
			return;
		}
		
		boolean isUnified;
		if ("unified".equals(args[0])) {
			isUnified = true;
		} else if ("splited".equals(args[0])) {
			isUnified = false;
		} else {
			System.out.println("1st arg is 'splited' or 'unified'");
			return;
		}
		
		try {
			KmlRewriter rewriter = new KmlRewriter(args[0], args[1], isUnified);
			rewriter.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
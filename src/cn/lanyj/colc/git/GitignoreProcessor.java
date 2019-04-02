package cn.lanyj.colc.git;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitignoreProcessor {

	List<String> contents = new ArrayList<>();

	public GitignoreProcessor(String content) {
		this.contents.addAll(Arrays.asList(content.split("\n")));
	}
	
	public List<String> getPatterns() {
		List<String> patterns = new ArrayList<>();
		// *, /
		for (String regex : contents) {
			boolean hasSplit = regex.indexOf('/') != -1 || regex.indexOf('\\') != -1;
			boolean endWithSplit = regex.endsWith("/") || regex.endsWith("\\");

			String prefix = "^", suffix = "$";
			
			regex = regex.replaceAll("[/\\\\]+", "/");
			
			for(String kp : Arrays.asList("^", ".")) {
				regex = regex.replaceAll("\\" + kp, "\\\\" + kp);
			}
			for(String kp : Arrays.asList("$", "(", ")", "{", "}")) {
				regex = regex.replaceAll("\\" + kp, "\\\\\\" + kp);
			}
			
			// \x is encoded
			// throw new IllegalArgumentException("character to be escaped is missing");
			regex = regex.replaceAll("/", "[/\\\\\\\\]");
			{
				// \*\*+ -> .*
				// \* -> [^/\\]*
				String[] tmp = regex.split("\\*\\*+");
				for(int i = 0; i < tmp.length; i++) {
					tmp[i] = tmp[i].replaceAll("\\*", "[^/\\\\\\\\]*");
				}
				regex = String.join("**", tmp);
				regex = regex.replaceAll("\\*\\*+", ".*");
			}
			
			if(endWithSplit) {
				suffix = ".*" + suffix;
			}
			if(hasSplit) {
				patterns.add(prefix + regex + suffix);
			} else {
				patterns.add(prefix + "(.+/)*" + regex + suffix);					
			}
		}
		return patterns;
	}
	
}

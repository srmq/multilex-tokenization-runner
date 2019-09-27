package org.srmq.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class MultilexCleanEmpty {

	public MultilexCleanEmpty() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param args First argument is parent directory to search for zero-sized
	 *             files. Second argument is the log file where deleted file names
	 *             will be recorded.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final Set<ImmutablePair<String, String>> zeroFilesWithParent = new HashSet<ImmutablePair<String, String>>();

		Consumer<Path> addToZero = p -> {
			File f = p.toFile();
			zeroFilesWithParent.add(ImmutablePair.of(f.getName(), f.getParentFile().getName()));
		};

		Files.find(Paths.get(new File(args[0]).toURI()), Integer.MAX_VALUE,
				(filePath, fileAttr) -> (fileAttr.isRegularFile() && fileAttr.size() == 0)).forEach(addToZero);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(args[1])))) {
			for (ImmutablePair<String, String> immutablePair : zeroFilesWithParent) {
				bw.write(immutablePair.right);
				bw.write('/');
				bw.write(immutablePair.left);
				bw.newLine();
			}
		}

		Consumer<Path> delete = p -> {
			try {
				Files.delete(p);
			} catch (IOException e) {
			}
		};

		Files.find(Paths.get(new File(args[0]).toURI()), Integer.MAX_VALUE, (filePath,
				fileAttr) -> (fileAttr.isRegularFile() && zeroFilesWithParent.contains(
						ImmutablePair.of(filePath.toFile().getName(), filePath.toFile().getParentFile().getName()))))
				.forEach(delete);

	}

}

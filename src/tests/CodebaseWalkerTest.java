package tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import tools.index.SourceFileWalker;

public class CodebaseWalkerTest
{

	@Test
	public void testRecursiveDirSearch()
	{
		String[] args = { "src/tests/samples/" };

		SourceFileWalker provider = new SourceFileWalker();

		try
		{
			String expected = "[src/tests/samples/test.c, src/tests/samples/subdir/test.c, src/tests/samples/tiff.cpp]";
			FilenameAggregator listener = new FilenameAggregator();
			provider.addListener(listener);
			provider.walk(args);

			// Fix 1: in Windows machine, the slash need to be changed.
			// Fix 2: TODO: the assertion will fail due to different orders of the file names.
			
			assertTrue(expected.equals(listener.filenames.toString().replace("\\", "/")));
		}
		catch (IOException e)
		{
			fail("IO Error");
		}
	}

}

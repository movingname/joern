package udg.useDefAnalysis.environments;

import java.util.Collection;
import java.util.LinkedList;

import udg.ASTProvider;
import udg.useDefGraph.UseOrDef;

public class EmitDefEnvironment extends UseDefEnvironment
{

	Collection<String> defSymbols = new LinkedList<String>();

	public void addChildSymbols(LinkedList<String> childSymbols,
			ASTProvider child)
	{
		if (isDef(child))
		{
			// add definition only for the last symbol, e.g.,
			// for x.y.z = 10, add a def for x.y.z only.

			if (childSymbols.size() > 1)
			{

				defSymbols.add(childSymbols.getLast());

			}
			else
				defSymbols.addAll(childSymbols);
		}
		if (isUse(child))
			symbols.addAll(childSymbols);
	}

	public Collection<UseOrDef> useOrDefsFromSymbols(ASTProvider child)
	{
		LinkedList<UseOrDef> retval = createDefsForAllSymbols(defSymbols);
		retval.addAll(createUsesForAllSymbols(symbols));
		return retval;
	}

}

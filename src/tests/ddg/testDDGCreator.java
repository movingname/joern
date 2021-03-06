package tests.ddg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.neo4j.graphdb.index.IndexHits;

import tests.TestDBTestsBatchInserter;
import tests.udg.CFGCreator;
import traversals.batchInserter.Elementary;
import traversals.batchInserter.Function;
import udg.CFGToUDGConverter;
import udg.useDefGraph.UseDefGraph;
import cfg.CFG;
import ddg.CFGAndUDGToDefUseCFG;
import ddg.DDGCreator;
import ddg.DataDependenceGraph.DDG;
import ddg.DataDependenceGraph.DefUseRelation;
import ddg.DefUseCFG.BatchInserterFactory;
import ddg.DefUseCFG.DefUseCFG;

public class testDDGCreator extends TestDBTestsBatchInserter
{

	@Test
	public void simplestTest()
	{
		// I guess the hits contain the definition of the function.
		IndexHits<Long> hits = Function.getFunctionsByName("ddg_simplest_test");
		long functionId = hits.next();
		DDGCreator ddgCreator = new DDGCreator();
		DDG ddg = ddgCreator.createForFunctionById(functionId);

		// Don't know why they call it reachesLinks.
		// Basically, it is just the edges in DDG
		Set<DefUseRelation> reachesLinks = ddg.getDefUseEdges();

		assertTrue(reachesLinks.size() == 1);

		for (DefUseRelation x : ddg.getDefUseEdges())
		{
			assertTrue((Elementary.getNodeCode((Long) x.src)
					.startsWith("int x = ")));
			assertTrue((Elementary.getNodeCode((long) x.dst)
					.startsWith("foo ( x )")));
			
			// It will print out x, the name of the variable.
			// System.out.println(x.symbol);
		}

	}

	@Test
	public void testConverter()
	{
		IndexHits<Long> hits = Function.getFunctionsByName("ddg_simplest_test");
		long funcId = hits.next();
		BatchInserterFactory cfgFactory = new BatchInserterFactory();
		DefUseCFG defUseCfgDb = cfgFactory.create(funcId);

		CFGCreator cfgCreator = new CFGCreator();
		CFGToUDGConverter converter = new CFGToUDGConverter();
		CFGAndUDGToDefUseCFG converter2 = new CFGAndUDGToDefUseCFG();

		CFG cfg = cfgCreator.getCFGForCode("f(){ int x = 0; foo(x);}");
		UseDefGraph udg = converter.convert(cfg);
		DefUseCFG defUseCfg = converter2.convert(cfg, udg);

		assertEquals(defUseCfg.getStatements().size(), defUseCfgDb
				.getStatements().size());
		assertEquals(defUseCfg.getParentBlocks().size(), defUseCfgDb
				.getParentBlocks().size());
		assertEquals(defUseCfg.getChildBlocks().size(), defUseCfgDb
				.getChildBlocks().size());

		assertEquals(defUseCfg.getSymbolsDefined().size(), defUseCfgDb
				.getSymbolsDefined().size());
		assertEquals(defUseCfg.getSymbolsUsed().size(), defUseCfgDb
				.getSymbolsUsed().size());

	}

}

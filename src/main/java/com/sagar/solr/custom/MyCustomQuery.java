package com.sagar.solr.custom;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;

public class MyCustomQuery extends CustomScoreQuery {

	public MyCustomQuery(Query subQuery) {
		super(subQuery);
	}

	@Override
	protected CustomScoreProvider getCustomScoreProvider(
			AtomicReaderContext context) throws IOException {
		return new MyScoreProvider(context);
	}

	// Inner Class
	class MyScoreProvider extends CustomScoreProvider {
		
		public MyScoreProvider(AtomicReaderContext context) {
			super(context);
		}

		@Override
		public float customScore(int doc, float subQueryScore, float valSrcScore)
				throws IOException {
			return customScore(doc, subQueryScore, new float[] { valSrcScore });
		}

		@Override
		public float customScore(int doc, float subQueryScore,
				float[] valSrcScores) throws IOException {
			// Method is called for every
			// matching document of the subQuery
			System.out.println("Custom Score called : " + doc + " - " + subQueryScore + " - " + valSrcScores);
			Document d = context.reader().document(doc);
			// plugin external score calculation based on the fields...
			List<IndexableField> fields = d.getFields();
			// and return the custom score
			System.out.println("Indexable Fields : " + fields.toString());
			float score = 1.0f;
			return score;

		}
	}

}

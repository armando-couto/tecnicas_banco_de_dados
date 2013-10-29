package br.com.unifor.algoritmos_joins.join;

import java.util.Hashtable;
import java.util.List;

import br.com.unifor.algoritmos_joins.database.RelationalTable;
import br.com.unifor.algoritmos_joins.database.Tuple;
import br.com.unifor.algoritmos_joins.database.exception.DatabaseException;
import br.com.unifor.algoritmos_joins.main.GlobalVars;
import br.com.unifor.algoritmos_joins.utils.ArrayUtils;

/**
 * @author armandocouto
 * @email coutoarmando@gmail.com
 * @date 29/10/2013
 */
public class HashJoin implements JoinAlgorithm {

	private Hashtable<Integer, Integer> hashTableArtist = new Hashtable<Integer, Integer>();
	private Hashtable<Integer, Integer> hashTableMusic = new Hashtable<Integer, Integer>();

	public RelationalTable join(RelationalTable artist, RelationalTable music) {
		String[] resultColumns = ArrayUtils.concat(artist.getColumnsNames(),
				music.getColumnsNames());

		RelationalTable resultTable = RelationalTable.newTable(
				GlobalVars.pageSize, resultColumns);
		try {
			List<Tuple> artistTuples = artist.getAllTuples();
			List<Tuple> musicTuples = music.getAllTuples();

			for (Tuple musicTuple : musicTuples) {
				Integer artistID = (Integer) musicTuple.get("ID_ARTIST");
				int bucket = hashFunction(artistID);
				hashTableMusic.put(bucket, artistID);
			}

			for (Tuple artistTuple : artistTuples) {
				Integer artistID = (Integer) artistTuple.get("ID");
				int bucket = hashFunction(artistID);
				hashTableArtist.put(bucket, artistID);
			}

			for (Tuple artistTuple : artistTuples) {
				for (Tuple musicTuple : musicTuples) {
					int bucketMusic = hashFunction((Integer) musicTuple
							.get("ID_ARTIST"));
					int bucketArtist = hashFunction((Integer) artistTuple
							.get("ID"));
					if (bucketMusic == bucketArtist) {
						Object[] valuesArray = ArrayUtils.concat(
								artistTuple.toArray(), musicTuple.toArray());
						resultTable.insert(resultColumns, valuesArray);
					}
				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		return resultTable;
	}

	private Integer hashFunction(Integer i) {
		return i;
	}
}

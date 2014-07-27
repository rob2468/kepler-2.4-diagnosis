package org.kepler.diagnosis;

import org.kepler.configuration.ConfigurationUtilities;
import org.kepler.provenance.QueryException;
import org.kepler.provenance.Queryable;
import org.kepler.provenance.sql.SQLQueryV8;
import org.kepler.util.ProvenanceStore;

import ptolemy.kernel.util.IllegalActionException;

// singleton, manager for all diagnosis tabs
public final class DiagnosisManager
{
	static private DiagnosisManager _instance = null;
	
	private ProvenanceStore _provenanceStore = null;
	
	private Queryable _queryable = null;
	private boolean _connected = false;
	
	public synchronized static DiagnosisManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new DiagnosisManager();
		}
		return _instance;
	}
	
	/**
	 * connect to provenance store via setting queryable to new SQLQueryV8
	 */
	public void connect()
	{
		if (!_connected)
		{
			try
			{
				_queryable = new SQLQueryV8(ConfigurationUtilities.getPairsMap(
						_provenanceStore.getProvenanceConfigurationProperty()));
				_connected = true;
			} catch (QueryException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConnected() {
		return _connected;
	}

	public void disconnect() throws IllegalActionException, QueryException {
	    if(_queryable != null) {
	        _queryable.disconnect();
	    }
		_connected = false;
	}

	public ProvenanceStore getProvenanceStore()
	{
		return _provenanceStore;
	}

	public void setProvenanceStore(ProvenanceStore _provenanceStore)
	{
		this._provenanceStore = _provenanceStore;
	}

	public Queryable getQueryable()
	{
		return _queryable;
	}

	public void setQueryable(Queryable _queryable)
	{
		this._queryable = _queryable;
	}
	
}

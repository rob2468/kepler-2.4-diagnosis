package org.kepler.diagnosis.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kepler.diagnosis.workflowmanager.gui.WorkflowRow;
import org.kepler.objectmanager.lsid.KeplerLSID;
import org.kepler.provenance.QueryException;
import org.kepler.provenance.sql.SQLQueryV8;

public class DiagnosisSQLQuery extends SQLQueryV8
{

	public DiagnosisSQLQuery(Map<String, String> parameters)
			throws QueryException
	{
		super(parameters);
	}
	
	public Integer getWorkflowID(KeplerLSID lsid) throws QueryException
	{
		Integer retval = null;
		
		try
		{
			PreparedStatement ps = _dbType.getPrepStatement("SELECT id "
					+ "FROM workflow "
					+ "WHERE lsid = ?");
			ps.setString(1, lsid.toStringWithoutRevision());
			ResultSet result = null;
			try
			{
				result = ps.executeQuery();
				if (result.next())
				{
					retval = result.getInt(1);
				}
			}
			finally
			{
				if(result != null)
                {
                    result.close();
                }
			}
		}
		catch(SQLException e)
        {
            throw new QueryException("Error querying workflow name: ", e);
        }
		
		return retval;
	}
	
	public List<Integer> getActorFireIDs(int runID, int actorID) throws QueryException
	{		
		try
		{
			PreparedStatement ps = _dbType.getPrepStatement("SELECT id "
					+ "FROM actor_fire "
					+ "where wf_exec_id = ? and actor_id = ?");
			ps.setInt(1, runID);
			ps.setInt(2, actorID);
			return _getIntResults(ps, 1);
		}
		catch(SQLException e)
        {
            throw new QueryException("Error querying workflow name: ", e);
        }
	}
	
	public ArrayList<WorkflowRow> getAllWorkflowIDAndName() throws QueryException
	{
		try
		{
			ArrayList<WorkflowRow> workflows = new ArrayList<WorkflowRow>();
			PreparedStatement ps = _dbType.getPrepStatement("SELECT id, name, lsid "
					+ "FROM workflow");
			ResultSet result = null;
			try
			{
				result = ps.executeQuery();
				while (result.next())
				{
					WorkflowRow workflow = new WorkflowRow();
					workflow.setId(result.getInt(1));
					workflow.setName(result.getString(2));
					workflow.setLsid(result.getString(3));
					workflows.add(workflow);
				}
			}
			finally
			{
				if(result != null)
                {
                    result.close();
                }
			}
			return workflows;
		}
		catch(SQLException e)
        {
            throw new QueryException("Error querying workflow name: ", e);
        }
	}
	
	/** Get a list of executions for a specific workflow. */
    public List<Integer> getExecutionsForWorkflow(int workflowID)
        throws QueryException
    {
    	try
    	{
    		PreparedStatement ps = _dbType.getPrepStatement("SELECT id "
    				+ "FROM workflow, workflow_exec "
    				+ "WHERE workflow.id = workflow_exec.wf_id AND workflow.id = ?");
    		ps.setInt(1, workflowID);
    		return _getIntResults(ps, 1);
    	}
    	catch(SQLException e)
        {
            throw new QueryException("Unable to query executions for " +
                "workflow id " + workflowID + ": ", e);
        }
    }
}

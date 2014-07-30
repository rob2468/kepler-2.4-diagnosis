package org.kepler.diagnosis.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
    /** Get an sequence of tokens for a port 
     *  @param portId the port id.
     *  @param last if true, the sequence starts at the last token created
     *  and goes backwards to the first; otherwise the sequence starts at
     *  the first.
     *  */
    public List<Integer> getTokensForPortID(Integer portId, boolean last) throws QueryException
    {
    	List<Integer> list = new LinkedList<Integer>();
    	
    	try
    	{
	    	ResultSet result = null;
	    	try
			{
	    		String readQueryStr = "SELECT pe.write_event_id "
	    				+ "FROM port_event pe "
	    				+ "WHERE pe.write_event_id != -1 AND pe.port_id = ?";
	    		String writeQueryStr = "SELECT pe.id "
	    				+ "FROM port_event pe "
	    				+ "WHERE pe.write_event_id = -1 AND pe.port_id = ?";
				PreparedStatement ps = _dbType.getPrepStatement(readQueryStr + " UNION ALL " + writeQueryStr);
				ps.setInt(1, portId);
				ps.setInt(2, portId);
				result = ps.executeQuery();
				while (result.next())
				{
					list.add(result.getInt(1));
				}
			}
	    	finally
	    	{
	    		if (result != null)
	    		{
	    			result.close();
	    		}
	    	}
    	}
    	catch(SQLException e)
        {
            throw new QueryException("Unable to retrieve for tokens: ", e);
        }
    	
    	Integer[] array = list.toArray(new Integer[0]);
        Arrays.sort(array);
        
        LinkedList<Integer> retval = new LinkedList<Integer>();
        
        for(int i = 0; i < array.length; i++)
        {
            if(last)
            {
                retval.addFirst(array[i]);
            }
            else
            {
                retval.addLast(array[i]);
            }
        }
        return retval;
    }
    
    public List<Integer> getWriteTokensForPortID(Integer portId) throws QueryException
    {
    	List<Integer> list = new LinkedList<Integer>();
    	
		try
		{
			ResultSet result = null;
			try
			{
				String queryStr = "SELECT pe.id "
						+ "FROM port_event pe "
						+ "WHERE pe.write_event_id = -1 AND pe.port_id = ?";
		    	PreparedStatement ps = _dbType.getPrepStatement(queryStr);
		    	ps.setInt(1, portId);
		    	result = ps.executeQuery();
		    	while (result.next())
				{
					list.add(result.getInt(1));
				}
			}
			finally
			{
				if (result != null)
	    		{
	    			result.close();
	    		}
			}
			return list;
		} catch (SQLException e)
		{
			throw new QueryException("Unable to retrieve for tokens: ", e);
		}
    }
    
    public List<Integer> getReadTokensForPortID(Integer portId) throws QueryException
    {
    	List<Integer> list = new LinkedList<Integer>();
    	
		try
		{
			ResultSet result = null;
			try
			{
				String queryStr = "SELECT pe.write_event_id "
						+ "FROM port_event pe "
						+ "WHERE pe.write_event_id != -1 AND pe.port_id = ?";
		    	PreparedStatement ps = _dbType.getPrepStatement(queryStr);
		    	ps.setInt(1, portId);
		    	result = ps.executeQuery();
		    	while (result.next())
				{
					list.add(result.getInt(1));
				}
			}
			finally
			{
				if (result != null)
	    		{
	    			result.close();
	    		}
			}
			return list;
		} catch (SQLException e)
		{
			throw new QueryException("Unable to retrieve for tokens: ", e);
		}
    }
    
    public List<Integer> getWriteTokensForExecutionAndPortID(int execId, Integer portId) throws QueryException
    {
    	List<Integer> list = new LinkedList<Integer>();
    	
		try
		{
			ResultSet result = null;
			try
			{
				String queryStr = "SELECT pe.id "
						+ "FROM port_event pe, actor_fire af "
						+ "WHERE pe.write_event_id = -1 AND pe.fire_id = af.id AND "
						+ "af.wf_exec_id = ? AND pe.port_id = ?";
		    	PreparedStatement ps = _dbType.getPrepStatement(queryStr);
		    	ps.setInt(1, execId);
		    	ps.setInt(2, portId);
		    	result = ps.executeQuery();
		    	while (result.next())
				{
					list.add(result.getInt(1));
				}
			}
			finally
			{
				if (result != null)
	    		{
	    			result.close();
	    		}
			}
			return list;
		} catch (SQLException e)
		{
			throw new QueryException("Unable to retrieve for tokens: ", e);
		}
    }
    
    public List<Integer> getReadTokensForExecutionAndPortID(int execId, Integer portId) throws QueryException
    {
    	List<Integer> list = new LinkedList<Integer>();
    	
		try
		{
			ResultSet result = null;
			try
			{
				String queryStr = "SELECT pe.write_event_id "
						+ "FROM port_event pe, actor_fire af "
						+ "WHERE pe.write_event_id != -1 AND pe.fire_id = af.id AND "
						+ "af.wf_exec_id = ? AND pe.port_id = ?";
		    	PreparedStatement ps = _dbType.getPrepStatement(queryStr);
		    	ps.setInt(1, execId);
		    	ps.setInt(2, portId);
		    	result = ps.executeQuery();
		    	while (result.next())
				{
					list.add(result.getInt(1));
				}
			}
			finally
			{
				if (result != null)
	    		{
	    			result.close();
	    		}
			}
			return list;
		} catch (SQLException e)
		{
			throw new QueryException("Unable to retrieve for tokens: ", e);
		}
    }
}

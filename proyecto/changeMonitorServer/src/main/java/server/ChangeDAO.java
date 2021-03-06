package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase que almacena los metodos correspondientes a los cambios
 */
public class ChangeDAO {

	/**
	 * A�ade un nuevo cambio en la base de datos local
	 */
	public void insertChange(ChangeVO change, Connection connection){
        try{
            /* Create "preparedStatement". */
            String queryString = "INSERT INTO cambios " +
                "(idDatos, FechaHora, cambio) VALUES (?, ?, ?)";                    
            PreparedStatement preparedStatement = 
                connection.prepareStatement(queryString);
            
            /* Fill "preparedStatement". */    
            preparedStatement.setInt(1, change.getIdData());
            preparedStatement.setTimestamp(2, change.getDate());
            preparedStatement.setBoolean(3, change.getChange());

            /* Execute query. */                    
            int insertedRows = preparedStatement.executeUpdate();
                
            if (insertedRows != 1) {
                throw new SQLException( "Problemas insertando cambios");
            }
                
        } 
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
                
    } 
	
}

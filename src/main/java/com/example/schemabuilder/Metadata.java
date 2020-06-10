package com.example.schemabuilder;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class Metadata {

    public DatabaseMetaData databaseMetaData;

    public void DisplayMetaData() throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:teiid:customer@mm://localhost:31000", "sa", "sa");
        databaseMetaData = connection.getMetaData();

        System.out.println("USER NAME : " + databaseMetaData.getUserName());
        System.out.println("DRIVER NAME : " + databaseMetaData.getDriverName());

        getColumnMetaData(getTableMetaData());
    }

    public ArrayList getTableMetaData() throws SQLException {
        String table[] = {"TABLE"};
        ResultSet resultSet = null;
        ArrayList tables = null;
        resultSet = databaseMetaData.getTables(null,null,null,table);
        tables = new ArrayList();
        while(resultSet.next()) {
            tables.add(resultSet.getString("TABLE_NAME"));
        }
        return tables;
    }

    public void getColumnMetaData(ArrayList<String> tables) throws SQLException {
        ResultSet resultSet = null;
        HashMap<String,String> data = null;

        for(String actualTable : tables) {
            resultSet = databaseMetaData.getColumns(null, null, (String) actualTable, null);

            System.out.println(actualTable.toUpperCase());

            while (resultSet.next()) {
                data = new HashMap<>();
                data.put(resultSet.getString("COLUMN_NAME"),resultSet.getString("TYPE_NAME"));

                for (Map.Entry<String,String> map : data.entrySet()) {

                    GraphQLScalarType returnType = ReturnType(map.getValue());

                    GraphQLObjectType newObject = GraphQLObjectType.newObject()
                            .name(map.getKey())
                            .field(GraphQLFieldDefinition.newFieldDefinition()
                                    .name(map.getValue())
                                    .type(returnType)
                            )
                            .build();
                    System.out.println(newObject);
                }
            }
        }
    }

    public GraphQLScalarType ReturnType(String type) {
        if(type.equals("long"))
            return Scalars.GraphQLLong;
        else if (type.equals("string"))
            return Scalars.GraphQLString;
        return null;
    }
}

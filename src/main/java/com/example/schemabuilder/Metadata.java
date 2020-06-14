package com.example.schemabuilder;

import graphql.Scalars;
import graphql.schema.*;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class Metadata {

    public static DatabaseMetaData databaseMetaData;

    public static ResultSet resultSet = null;
    public static HashMap<String,String> data = null;
    public static GraphQLSchema graphQLSchema = null;
    public static GraphQLObjectType graphQLObjectType = null;



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

        for(String actualTable : tables) {
            resultSet = databaseMetaData.getColumns(null, null, (String) actualTable, null);

            graphQLObjectType = GraphQLObjectType.newObject()
                                    .name("Query")
                                    .build();

            while (resultSet.next()) {
                data  = new HashMap<>();
                data.put(resultSet.getString("COLUMN_NAME"), resultSet.getString("TYPE_NAME"));

                graphQLObjectType = GraphQLObjectType.newObject()
                        .name(actualTable)
                        .fields(getFields())
                        .field(GraphQLFieldDefinition.newFieldDefinition().name("ANY").type(Scalars.GraphQLString).build())
                        .build();

                graphQLSchema = GraphQLSchema.newSchema()
                        .query(graphQLObjectType)
                        .build();

                SchemaPrinter sp = new SchemaPrinter();
                String schema = sp.print(graphQLSchema);
                System.out.println(schema);
            }
        }
    }


    public static List<GraphQLFieldDefinition> getFields() {

        List<GraphQLFieldDefinition> list = new ArrayList<>();

        for (Map.Entry<String,String> hm : data.entrySet()) {

            list.add(GraphQLFieldDefinition.newFieldDefinition()
                    .name(hm.getKey())
                    .type(ReturnType(hm.getValue()))
                    .build());
        }

//        for(GraphQLFieldDefinition s : list)
//            System.out.println(s);

        return list;
    }

    public static GraphQLScalarType ReturnType(String type) {
        if(type.equals("long"))
            return Scalars.GraphQLLong;
        else if (type.equals("string"))
            return Scalars.GraphQLString;
        return null;
    }
}

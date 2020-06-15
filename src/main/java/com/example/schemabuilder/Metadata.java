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
    public static GraphQLObjectType objectType = null;
    public static ArrayList<String> tables = null;

    public static void DisplayMetaData() throws Exception {

        Connection connection = DriverManager.getConnection("jdbc:teiid:customer@mm://localhost:31000", "sa", "sa");
        databaseMetaData = connection.getMetaData();

        System.out.println("USER NAME : " + databaseMetaData.getUserName());
        System.out.println("DRIVER NAME : " + databaseMetaData.getDriverName());

        getTableMetaData();
    }

    public static void getTableMetaData() throws Exception {

        String table[] = {"TABLE"};

        resultSet = databaseMetaData.getTables(null,null,null,table);
        tables = new ArrayList();

        while(resultSet.next()) {
            tables.add(resultSet.getString("TABLE_NAME"));
        }

        for(String actualTable : tables) {

            createSchema(actualTable);

        }
    }

    public static void createSchema(String tableName) throws Exception {
        resultSet = databaseMetaData.getColumns(null, null, (String) tableName, null);

        GraphQLObjectType.Builder graphQLObjectType = GraphQLObjectType.newObject();

        System.out.println("-------------Schema for Table " + tableName + "----------------");

        while (resultSet.next()) {
            data  = new HashMap<>();
            data.put(resultSet.getString("COLUMN_NAME"), resultSet.getString("TYPE_NAME"));

            for (Map.Entry<String, String> hm : data.entrySet()) {

                graphQLObjectType
                        .name(tableName)
                        .field(GraphQLFieldDefinition.newFieldDefinition()
                                .name(hm.getKey())
                                .type(ReturnType(hm.getValue())));
                objectType = graphQLObjectType.build();

            }
        }
        graphQLSchema = GraphQLSchema.newSchema()
                    .query(objectType)
                    .build();

            SchemaPrinter sp = new SchemaPrinter();
            String schema = sp.print(graphQLSchema);
            System.out.println(schema);
    }


    public static GraphQLScalarType ReturnType(String type) {
        if(type.equals("long"))
            return Scalars.GraphQLLong;
        else if (type.equals("string"))
            return Scalars.GraphQLString;
        return null;
    }
}

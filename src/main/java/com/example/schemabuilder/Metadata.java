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
    public static ArrayList<String> tables = null;
    public static SchemaPrinter schemaPrinter = new SchemaPrinter();


    public static void DisplayMetaData() throws Exception {

        Connection connection = DriverManager.getConnection("jdbc:teiid:customer@mm://localhost:31000", "sa", "sa");
        databaseMetaData = connection.getMetaData();

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

        while (resultSet.next()) {

                graphQLObjectType
                        .name(tableName.toLowerCase())
                        .field(GraphQLFieldDefinition.newFieldDefinition()
                                .name(resultSet.getString("COLUMN_NAME"))
                                .type(GraphQLNonNull.nonNull(ReturnType(resultSet.getString("TYPE_NAME")))))
                        .build();
        }

            String printSchema = schemaPrinter.print(graphQLObjectType.build());
            System.out.println(printSchema);
    }


    public static GraphQLScalarType ReturnType(String type) {
        if(type.equals("long"))
            return Scalars.GraphQLLong;
        else if (type.equals("string"))
            return Scalars.GraphQLString;
        return null;
    }
}

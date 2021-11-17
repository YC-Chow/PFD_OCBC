package sg.edu.np.pfd_ocbc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    private final static  int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Transfer.db";
    private static final String TABLE_TRANSFER = "Transfer";
    private static final String TRANSFER_COLUMN_UNIQUECODE = "UniqueCode";
    private static final String TRANSFER_COLUMN_SENDER = "SenderAcc";
    private static final String TRANSFER_COLUMN_RECEIVER = "ReceiverAcc";
    private static final String TRANSFER_COLUMN_RECEIVERNAME = "ReceiverName";
    private static final String TRANSFER_COLUMN_AMOUNT = "Amount";



    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRANSFER_TABLE = "CREATE TABLE " + TABLE_TRANSFER + "(" + TRANSFER_COLUMN_UNIQUECODE + " TEXT PRIMARY KEY,"
                + TRANSFER_COLUMN_SENDER + " TEXT," + TRANSFER_COLUMN_RECEIVER + " TEXT,"
                + TRANSFER_COLUMN_RECEIVERNAME + " TEXT,"
                + TRANSFER_COLUMN_AMOUNT + " DOUBLE)";
        db.execSQL(CREATE_TRANSFER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSFER);
    }

    //Stores required information to make transaction
    //Account for failed transaction
    public void MakeTransaction(Transaction transaction)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRANSFER_COLUMN_SENDER, transaction.getSenderAccNo());
        values.put(TRANSFER_COLUMN_RECEIVER, transaction.getRecipientAccNo());
        values.put(TRANSFER_COLUMN_AMOUNT, transaction.getTransactionAmt());
        values.put(TRANSFER_COLUMN_UNIQUECODE, transaction.getTransactionId());
        values.put(TRANSFER_COLUMN_RECEIVERNAME, transaction.getRecipientName());

        db.insert(TABLE_TRANSFER, null, values);
        db.close();
    }

    public void DeleteTransaction(String unique)
    {
        SQLiteDatabase db = this.getWritableDatabase();


        db.delete(TABLE_TRANSFER, TRANSFER_COLUMN_UNIQUECODE + " = \"" + unique + "\""  , null);
        db.close();
    }

    public Transaction CheckFailedTransaction(String accNo)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String dbQuery = "SELECT * FROM " + TABLE_TRANSFER + " WHERE " + TRANSFER_COLUMN_SENDER + " = \"" + accNo + "\"";
        Cursor cursor = db.rawQuery(dbQuery, null);
        Transaction transaction = new Transaction();
        if (cursor.moveToFirst())
        {
            transaction.setUniqueCode(cursor.getString(0));
            transaction.setSenderAccNo(cursor.getString(1));
            transaction.setRecipientAccNo(cursor.getString(2));
            transaction.setRecipientName(cursor.getString(3));
            transaction.setTransactionAmt(cursor.getDouble(4));
        }
        else
        {
            transaction = null;
        }
        cursor.close();
        db.close();
        return transaction;
    }
}

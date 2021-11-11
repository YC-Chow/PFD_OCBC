package sg.edu.np.pfd_ocbc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

public class DBHandler extends SQLiteOpenHelper {

    private final static  int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Transfer.db";
    private static final String TABLE_TRANSFER = "Transfer";
    private static final String TRANSFER_COLUMN_ID = "_id";
    private static final String TRANSFER_COLUMN_SENDER = "SenderAcc";
    private static final String TRANSFER_COLUMN_RECEIVER = "ReceiverAcc";
    private static final String TRANSFER_COLUMN_AMOUNT = "Amount";
    private static final String TRANSFER_COLUMN_DATE ="Date";


    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRANSFER_TABLE = "CREATE TABLE " + TABLE_TRANSFER + "(" + TRANSFER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TRANSFER_COLUMN_SENDER + " TEXT," + TRANSFER_COLUMN_RECEIVER + " TEXT," + TRANSFER_COLUMN_AMOUNT + " DOUBLE,"
                + TRANSFER_COLUMN_DATE + " TEXT)";
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
        values.put(TRANSFER_COLUMN_RECEIVER, transaction.getToBankNum());
        values.put(TRANSFER_COLUMN_AMOUNT, transaction.getTransactionAmt());
        values.put(TRANSFER_COLUMN_DATE, transaction.getTransactionDate());

        db.insert(TABLE_TRANSFER, null, values);
        db.close();
    }

    public void DeleteTransaction(Transaction transaction)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = TRANSFER_COLUMN_SENDER + " = " + transaction.getSenderAccNo()
                + " && " + TRANSFER_COLUMN_RECEIVER + " = " + transaction.getToBankNum()
                + " && " + TRANSFER_COLUMN_DATE + " = " + new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(transaction.getTransactionDate());

        db.delete(TABLE_TRANSFER, whereClause, null);
        db.close();
    }

    public boolean CheckFailedTransaction(String accNo)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = false;
        String dbQuery = "SELECT * FROM " + TABLE_TRANSFER + " WHERE " + TRANSFER_COLUMN_SENDER + " = " + accNo;
        Cursor cursor = db.rawQuery(dbQuery, null);
        if (cursor.moveToFirst())
        {
            result = true;
        }

        return  result;
    }
}

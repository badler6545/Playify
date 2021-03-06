package badler.com.playify2;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomGrid extends BaseAdapter{

    String [] result = new String[3];
    Context context;
    Bitmap [] imageId;
    private static LayoutInflater inflater=null;
    public CustomGrid(MainActivity mainActivity, Bitmap[] artwork, String[] albums) {
        // TODO Auto-generated constructor stub
        for (int x = 0; x < 3; x++)
        {
            result[x] = albums[x];
        }
        context=mainActivity;
        imageId=artwork;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);

            if (position < 3) {
                holder.tv.setText(result[position]);
                holder.img.setImageBitmap(imageId[position]);
            }

        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked " + result[position], Toast.LENGTH_LONG).show();
            }

        });


        return rowView;
    }

}
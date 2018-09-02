package com.superiorshoes.ooguro.androidsuperiorserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.superiorshoes.ooguro.androidsuperiorserver.Common.Common;
import com.superiorshoes.ooguro.androidsuperiorserver.Interface.ItemClickListener;
import com.superiorshoes.ooguro.androidsuperiorserver.R;

public class ClothViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener

{

    public TextView cloth_name;
    public ImageView cloth_image;

    private ItemClickListener itemClickListener;

    public ClothViewHolder(View itemView) {
        super(itemView);

        cloth_name = (TextView)itemView.findViewById(R.id.cloth_name);
        cloth_image = (ImageView)itemView.findViewById(R.id.cloth_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view,getAdapterPosition(),false);

    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);

    }
}

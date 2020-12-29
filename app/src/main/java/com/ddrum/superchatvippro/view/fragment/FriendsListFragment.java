package com.ddrum.superchatvippro.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ddrum.superchatvippro.adapter.FriendAdapter;
import com.ddrum.superchatvippro.adapter.RequestAdapter;
import com.ddrum.superchatvippro.R;
import com.ddrum.superchatvippro.constant.Constant;
import com.ddrum.superchatvippro.model.User;
import com.ddrum.superchatvippro.view.activity.ChatActivity;
import com.ddrum.superchatvippro.view.activity.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class FriendsListFragment extends Fragment {
    //Widget
    private AppCompatEditText edtSearch;
    private FriendAdapter friendAdapter;
    private RequestAdapter friendRequestAdapter;
    //Database && User
    private DatabaseReference reference;
    private FirebaseUser currentUser;
    //ViewModel
    private MainViewModel viewModel;
    private AppCompatTextView tvRequest;
    private AppCompatTextView tvFriends;
    private RecyclerView rcvFriendList;
    private RecyclerView rcvFriendRequest;


    public static FriendsListFragment newInstance() {

        Bundle args = new Bundle();

        FriendsListFragment fragment = new FriendsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class); //nhu loz
        initView(view);
        initDatabase();


//Friend request
        Query queryRequest = reference.child(Constant.RECEIVER).child(currentUser.getUid());
        friendRequestAdapter = new RequestAdapter(requireActivity(), viewModel, queryRequest);
        rcvFriendRequest.setAdapter(friendRequestAdapter);
        rcvFriendRequest.setLayoutManager(new LinearLayoutManager(requireContext())); //requireContext())

        friendRequestAdapter.setOnCLick(new RequestAdapter.Callback() {
            String currentUserId = currentUser.getUid();
            @Override
            public void onClickAccept(int position, String otherUserName, String otherUserId) {
                // Thêm trường Friend
                User otherUser = new User();
                otherUser.setId(otherUserId);
                otherUser.setUsername(otherUserName);
                reference.child(Constant.FRIEND).child(currentUserId).child(otherUserId).setValue(otherUser);

                User currentUser = new User();
                currentUser.setId(currentUserId);
                currentUser.setUsername(viewModel.currentUser.getValue().getUsername());
                reference.child(Constant.FRIEND).child(otherUserId).child(currentUserId).setValue(currentUser);

                // Xoá trường Request
                reference.child(Constant.RECEIVER).child(currentUserId).child(otherUserId).setValue(null);
                reference.child(Constant.SENDER).child(otherUserId).child(currentUserId).setValue(null);
            }

            @Override
            public void onClickDeny(int position, String otherUserId) {
                reference.child(Constant.RECEIVER).child(currentUserId).child(otherUserId).setValue(null);
                reference.child(Constant.SENDER).child(otherUserId).child(currentUserId).setValue(null);
            }

        });





// Friend list
        Query queryFriend = reference.child(Constant.FRIEND).child(currentUser.getUid());
        friendAdapter = new FriendAdapter(requireContext(), viewModel, queryFriend);
        rcvFriendList.setAdapter(friendAdapter);
        rcvFriendList.setLayoutManager(new LinearLayoutManager(requireContext()));
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Query trường tên (username)
                Query query = reference.child(Constant.FRIEND).child(currentUser.getUid())
                        .orderByChild("username").startAt(s.toString()).endAt(s + "\uf8ff");
                friendAdapter = new FriendAdapter(requireContext(), viewModel, query);
                rcvFriendList.setAdapter(friendAdapter);
                unFriendClick();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        unFriendClick();
    }


    private void unFriendClick() {
        friendAdapter.setOnUnfriendClick(new FriendAdapter.Callback() {
            @Override
            public void onClickUnFriend(int position, String userOtherId) {
                String currentUserId = currentUser.getUid();
                reference.child(Constant.FRIEND).child(currentUserId).child(userOtherId).setValue(null);
                reference.child(Constant.FRIEND).child(userOtherId).child(currentUserId).setValue(null);
            }

            @Override
            public void onItemClick(int position, String userOtherId) {
                Intent intent = new Intent(requireContext(), ChatActivity.class);
                intent.putExtra("otherId", userOtherId);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }




    private void initView(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
        tvRequest = view.findViewById(R.id.tv_request);
        tvFriends = view.findViewById(R.id.tv_friends);
        rcvFriendList = view.findViewById(R.id.rcvFriendList);
        rcvFriendRequest = view.findViewById(R.id.rcv_FriendRequest);
    }

    public void initDatabase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }
}
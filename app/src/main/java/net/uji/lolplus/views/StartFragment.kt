package net.uji.lolplus.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.fragment_start.*

import net.uji.lolplus.R
import net.uji.lolplus.presenter.StartPresenter


class StartFragment : Fragment() {

    val videos = arrayListOf ("3Eu7NzzHC84","aR-KAldshAE","zF5Ddo9JdpY","vzHrjOMfHPY","ZjvDFvzfxsQ")
    private lateinit var presenter: StartPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.show()
        presenter = StartPresenter(this)
        //loadvideo()
        btnlink.setOnClickListener{presenter.link()}
    }

    fun showVideo(videoId: String) {
        val ytVideo: YouTubePlayerView = requireActivity().findViewById (R.id.youtube_view)
        lifecycle.addObserver (ytVideo)
        ytVideo.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
    }
    fun openLink(url: String) {
        val myintent = Intent(Intent.ACTION_VIEW)
        myintent.data = Uri.parse(url)
        startActivity(myintent)
    }
}

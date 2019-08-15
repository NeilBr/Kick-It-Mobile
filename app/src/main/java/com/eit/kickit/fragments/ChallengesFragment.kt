package com.eit.kickit.fragments

/**
 * Sets up the recycler view for the layout
 * This fragment is connected to a single bucket list.
 * Diplays the challenges of that bucket list
 *
 * TO DO: When selecting a challenge open view challenge layout
 *
 *
 *
 *
class ChallengesFragment : Fragment() {

    private val temp : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        for (i in 1..100)
        {
            temp.add("Yeet")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return LayoutInflater.from(container?.context).inflate(R.layout.activity_challenges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvChallenges.layoutManager = LinearLayoutManager(activity)
      //  rvChallenges.adapter = Challenge_Adapter(temp)
    }
}
        */
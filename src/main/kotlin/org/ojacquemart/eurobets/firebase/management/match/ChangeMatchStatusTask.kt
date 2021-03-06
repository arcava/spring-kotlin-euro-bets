package org.ojacquemart.eurobets.firebase.management.match

import org.ojacquemart.eurobets.firebase.Collections
import org.ojacquemart.eurobets.firebase.config.FirebaseRef
import org.ojacquemart.eurobets.firebase.config.SchedulingSettings
import org.ojacquemart.eurobets.firebase.misc.Status
import org.ojacquemart.eurobets.firebase.rx.RxFirebase
import org.ojacquemart.eurobets.lang.loggerFor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component
import rx.Observable
import java.util.concurrent.ScheduledFuture
import javax.annotation.PostConstruct

@Component
open class ChangeMatchStatusTask(val schedulingConfig: SchedulingSettings,
                                 val ref: FirebaseRef,
                                 val taskScheduler: TaskScheduler) {

    private val log = loggerFor<org.ojacquemart.eurobets.firebase.management.match.ChangeMatchStatusTask>()

    var scheduled: ScheduledFuture<*>? = null

    @PostConstruct
    fun changeStatuses() {
        val obsMatches: Observable<List<Match>> = RxFirebase.observeList(ref.firebase.child(Collections.matches), Match::class.java)
                .map { matches -> matches.filter { match -> match.status == Status.TO_PLAY.id } }

        obsMatches.subscribe { matches ->
            if (!matches.isEmpty()) {
                handleMatches(matches)
            } else {
                log.info("All matches have been started!")
            }
        }
    }

    private fun handleMatches(matches: List<Match>) {
        log.info("Schedule task to update ${matches.size} matches")
        if (scheduled != null) scheduled!!.cancel(true)

        scheduled = taskScheduler.schedule(ChangeMatchCheckTaskRunnable(matches, ref), CronTrigger(schedulingConfig.cronMatches))
    }

}
/*
 * Copyright 2013, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.async;

import com.google.common.base.Optional;

import lombok.Getter;

/**
 * An Asynchronous handle that has facility methods to time the duration of the
 * task.
 *
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class TimedAsyncHandle<V> extends AsyncTaskHandle<V> {

    @Getter
    private long startTime = -1;

    @Getter
    private long finishTime = -1;

    public TimedAsyncHandle(String taskName) {
        super(taskName);
    }

    public void startTiming() {
        startTime = System.currentTimeMillis();
    }

    public void finishTiming() {
        finishTime = System.currentTimeMillis();
    }

    /**
     * @return An optional container with the estimated time remaining for the
     *         process to finish, or an empty container if the time cannot be
     *         estimated.
     */
    public Optional<Long> getEstimatedTimeRemaining() {
        if (this.startTime > 0 && currentProgress > 0) {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - this.startTime;
            int remainingUnits = this.maxProgress - this.currentProgress;
            return Optional.of(timeElapsed * remainingUnits
                    / this.currentProgress);
        } else {
            return Optional.absent();
        }
    }

    /**
     * @return The time that the task has been executing for, or the total
     *         execution time if the task has finished (in milliseconds).
     */
    public long getExecutingTime() {
        if (startTime > 0) {
            if (finishTime > startTime) {
                return finishTime - startTime;
            } else {
                return System.currentTimeMillis() - startTime;
            }
        } else {
            return 0;
        }
    }

    /**
     * @return The estimated elapsed time (in milliseconds) from the start of
     *         the process.
     */
    public long getTimeSinceStart() {
        if (this.startTime > 0) {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - this.startTime;
            return timeElapsed;
        } else {
            return 0;
        }
    }

    /**
     * @return The estimated elapsed time (in milliseconds) from the finish of
     *         the process.
     */
    public long getTimeSinceFinish() {
        if (finishTime > 0) {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - finishTime;
            return timeElapsed;
        } else {
            return 0;
        }
    }
}

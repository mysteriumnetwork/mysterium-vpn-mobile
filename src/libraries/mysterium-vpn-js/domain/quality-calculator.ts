/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import { Metrics } from '../models/metrics'
import { QualityLevel } from '../models/quality-level'

export class QualityCalculator {
  /**
   * Calculates quality number for given metrics.
   *
   * @return number between 0 and 1. If metrics are empty, null is returned.
   */
  public calculateValue (metrics: Metrics): number | null {
    const counts = metrics.connectCount
    const total = counts.success + counts.fail + counts.timeout

    if (total === 0) {
      return null
    }

    return counts.success / total
  }

  public calculateLevel (quality: number | null) {
    if (quality === null) {
      return QualityLevel.UNKNOWN
    }
    if (quality >= HIGH_QUALITY) {
      return QualityLevel.HIGH
    }
    if (quality >= MEDIUM_QUALITY) {
      return QualityLevel.MEDIUM
    }
    return QualityLevel.LOW
  }
}

const MEDIUM_QUALITY = 0.2
const HIGH_QUALITY = 0.5
